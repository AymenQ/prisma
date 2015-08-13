package com.puzzletimer;

import com.puzzletimer.database.*;
import com.puzzletimer.gui.MainFrame;
import com.puzzletimer.models.*;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.puzzles.PuzzleProvider;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.*;
import com.puzzletimer.state.MessageManager.MessageType;
import com.puzzletimer.statistics.Best;
import com.puzzletimer.statistics.BestAverage;
import com.puzzletimer.statistics.BestMean;
import com.puzzletimer.statistics.StatisticalMeasure;
import com.puzzletimer.timer.Timer;
import com.puzzletimer.tips.TipProvider;
import com.puzzletimer.util.SolutionUtils;
import org.h2.tools.RunScript;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.puzzletimer.Internationalization._;

public class Main {
    private ConfigurationDAO configurationDAO;
    private ColorDAO colorDAO;
    private CategoryDAO categoryDAO;
    private SolutionDAO solutionDAO;

    private MessageManager messageManager;
    private ConfigurationManager configurationManager;
    private TimerManager timerManager;
    private PuzzleProvider puzzleProvider;
    private ColorManager colorManager;
    private ScrambleParserProvider scrambleParserProvider;
    private ScramblerProvider scramblerProvider;
    private TipProvider tipProvider;
    private CategoryManager categoryManager;
    private ScrambleManager scrambleManager;
    private SolutionManager solutionManager;
    private SessionManager sessionManager;

    private final static String DATABASE_RESOURCE_LOCATION = "/com/puzzletimer/resources/database/";

    public Main() {
        // load database driver
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(
                    frame,
                    _("main.database_driver_load_error"),
                    _("main.prisma_puzzle_timer"),
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // create initial database if necessary
        File databaseFile = new File("puzzletimer.h2.db");
        if (!databaseFile.exists()) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:h2:puzzletimer", "sa", "");
                Reader script = new InputStreamReader(
                        getClass().getResourceAsStream(
                                DATABASE_RESOURCE_LOCATION + "puzzletimer0.3.sql"));
                RunScript.execute(connection, script);
                connection.close();
            } catch (SQLException e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(
                        frame,
                        String.format(_("main.database_error_message"), e.getMessage()),
                        _("main.prisma_puzzle_timer"),
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        // connect to database
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:h2:puzzletimer;IFEXISTS=TRUE", "sa", "");
        } catch (SQLException e) {
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(
                    frame,
                    _("main.concurrent_database_access_error_message"),
                    _("main.prisma_puzzle_timer"),
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // update database if necessary
        List<String> versions = Arrays.asList("0.3", "0.4", "0.5", "0.6", "0.9", "0.9.3", "0.10.0");

        String currentVersion = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT VALUE FROM CONFIGURATION WHERE KEY = 'VERSION'");
            while (resultSet.next()) {
                currentVersion = resultSet.getString(1);
            }
        } catch (SQLException e) {
        }

        int maxVersionIndex = versions.size() - 1;
        int versionIndex = versions.indexOf(currentVersion);
        if (versionIndex < 0)
            versionIndex = maxVersionIndex;

        for (versionIndex++; versionIndex <= maxVersionIndex; versionIndex++) {
            try {
                String scriptName = "puzzletimer" + versions.get(versionIndex) + ".sql";
                Reader script = new InputStreamReader(
                        getClass().getResourceAsStream(
                                DATABASE_RESOURCE_LOCATION + scriptName));
                RunScript.execute(connection, script);
            } catch (SQLException e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(
                        frame,
                        String.format(_("main.database_error_message"), e.getMessage()),
                        _("main.prisma_puzzle_timer"),
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        // message manager
        this.messageManager = new MessageManager();

        // configuration DAO
        this.configurationDAO = new ConfigurationDAO(connection);

        // configuration manager
        this.configurationManager = new ConfigurationManager(this.configurationDAO.getAll());
        this.configurationManager.addListener(new ConfigurationManager.Listener() {
            @Override
            public void configurationEntryUpdated(String key, String value) {
                try {
                    Main.this.configurationDAO.update(
                            new ConfigurationEntry(key, value));
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }
        });

        // timer manager
        this.timerManager = new TimerManager();
        this.timerManager.setInspectionEnabled(
                this.configurationManager.getConfiguration("INSPECTION-TIME-ENABLED").equals("TRUE"));
        this.timerManager.setAnyKeyEnabled(
                this.configurationManager.getConfiguration("ANYKEY-ENABLED").equals("TRUE"));
        this.timerManager.setHideTimerEnabled(
                this.configurationManager.getConfiguration("HIDETIMER-ENABLED").equals("TRUE"));
        this.timerManager.setSmoothTimingEnabled(
                this.configurationManager.getConfiguration("SMOOTH-TIMING-ENABLED").equals("TRUE"));
        this.timerManager.addListener(new TimerManager.Listener() {
            @Override
            public void solutionFinished(Timing timing, String penalty) {
                // add solution
                Main.this.solutionManager.addSolution(
                        new Solution(
                                UUID.randomUUID(),
                                Main.this.categoryManager.getCurrentCategory().getCategoryId(),
                                Main.this.scrambleManager.getCurrentScramble(),
                                timing,
                                penalty,
                                ""));

                // check for personal records
                StatisticalMeasure[] measures = {
                        new Best(1, Integer.MAX_VALUE),
                        new BestMean(3, 3),
                        new BestMean(100, 100),
                        new BestAverage(5, 5),
                        new BestAverage(12, 12),
                        new BestAverage(50, 50),
                };

                String[] descriptions = {
                        _("main.single"),
                        _("main.mean_of_3"),
                        _("main.mean_of_100"),
                        _("main.average_of_5"),
                        _("main.average_of_12"),
                        _("main.average_of_50"),
                };

                Solution[] solutions = Main.this.solutionManager.getSolutions();
                Solution[] sessionSolutions = Main.this.sessionManager.getSolutions();

                for (int i = 0; i < measures.length; i++) {
                    if (sessionSolutions.length < measures[i].getMinimumWindowSize()) {
                        continue;
                    }

                    measures[i].setSolutions(solutions, Main.this.configurationManager.getConfiguration("TIMER-PRECISION").equals("CENTISECONDS"));
                    long allTimeBest = measures[i].getValue();

                    measures[i].setSolutions(sessionSolutions, Main.this.configurationManager.getConfiguration("TIMER-PRECISION").equals("CENTISECONDS"));
                    long sessionBest = measures[i].getValue();

                    if (measures[i].getWindowPosition() == 0 && sessionBest <= allTimeBest) {
                        Main.this.messageManager.enqueueMessage(
                                MessageType.INFORMATION,
                                String.format(_("main.personal_record_message"),
                                        Main.this.categoryManager.getCurrentCategory().getDescription(),
                                        SolutionUtils.formatMinutes(measures[i].getValue(), Main.this.configurationManager.getConfiguration("TIMER-PRECISION"), measures[i].getRound()),
                                        descriptions[i]));
                    }
                }

                // gerate next scramble
                Main.this.scrambleManager.changeScramble();
            }

            @Override
            public void timerChanged(Timer timer) {
                Main.this.configurationManager.setConfiguration(
                        "TIMER-TRIGGER", timer.getTimerId());
            }

            @Override
            public void inspectionEnabledSet(boolean inspectionEnabled) {
                Main.this.configurationManager.setConfiguration(
                        "INSPECTION-TIME-ENABLED", inspectionEnabled ? "TRUE" : "FALSE");
            }

            @Override
            public void anyKeyEnabledSet(boolean anyKeyEnabled) {
                Main.this.configurationManager.setConfiguration(
                        "ANYKEY-ENABLED", anyKeyEnabled ? "TRUE" : "FALSE");
            }

            @Override
            public void hideTimerEnabledSet(boolean hideTimerEnabled) {
                Main.this.configurationManager.setConfiguration(
                        "HIDETIMER-ENABLED", hideTimerEnabled ? "TRUE" : "FALSE");
            }

            @Override
            public void precisionChanged(String timerPrecisionId) {
                Main.this.configurationManager.setConfiguration("TIMER-PRECISION", timerPrecisionId);
            }

            @Override
            public void smoothTimingSet(boolean smoothTimingEnabled) {
                Main.this.configurationManager.setConfiguration(
                        "SMOOTH-TIMING-ENABLED", smoothTimingEnabled ? "TRUE" : "FALSE");
            }
        });

        // puzzle provider
        this.puzzleProvider = new PuzzleProvider();

        // color DAO
        this.colorDAO = new ColorDAO(connection);

        // color manager
        this.colorManager = new ColorManager(this.colorDAO.getAll());
        this.colorManager.addListener(new ColorManager.Listener() {
            @Override
            public void colorSchemeUpdated(ColorScheme colorScheme) {
                try {
                    Main.this.colorDAO.update(colorScheme);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }
        });

        // scramble parser provider
        this.scrambleParserProvider = new ScrambleParserProvider();

        // scrambler provider
        this.scramblerProvider = new ScramblerProvider();


        // tip provider
        this.tipProvider = new TipProvider();

        // category DAO
        this.categoryDAO = new CategoryDAO(connection);

        // categoryManager
        Category[] categories = this.categoryDAO.getAll();

        UUID currentCategoryId = UUID.fromString(
                this.configurationManager.getConfiguration("CURRENT-CATEGORY"));
        Category currentCategory = null;
        for (Category category : categories) {
            if (category.getCategoryId().equals(currentCategoryId)) {
                currentCategory = category;
            }
        }

        this.categoryManager = new CategoryManager(categories, currentCategory);
        this.categoryManager.addListener(new CategoryManager.Listener() {
            @Override
            public void currentCategoryChanged(Category category) {
                Main.this.configurationManager.setConfiguration(
                        "CURRENT-CATEGORY",
                        category.getCategoryId().toString());

                try {
                    Main.this.solutionManager.loadSolutions(
                            Main.this.solutionDAO.getAll(category));
                    Main.this.sessionManager.clearSession();
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }

            @Override
            public void categoryAdded(Category category) {
                try {
                    Main.this.categoryDAO.insert(category);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }

            @Override
            public void categoryRemoved(Category category) {
                try {
                    Main.this.categoryDAO.delete(category);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }

            @Override
            public void categoryUpdated(Category category) {
                try {
                    Main.this.categoryDAO.update(category);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }
        });

        // scramble manager
        this.scrambleManager = new ScrambleManager(
                this.scramblerProvider,
                this.scramblerProvider.get(currentCategory.getScramblerId()));
        this.categoryManager.addListener(new CategoryManager.Listener() {
            @Override
            public void currentCategoryChanged(Category category) {
                Main.this.scrambleManager.setCategory(category);
            }
        });

        // solution DAO
        this.solutionDAO = new SolutionDAO(connection, this.scramblerProvider, this.scrambleParserProvider);

        // solution manager
        this.solutionManager = new SolutionManager();
        this.solutionManager.addListener(new SolutionManager.Listener() {
            @Override
            public void solutionAdded(Solution solution) {
                Main.this.sessionManager.addSolution(solution);

                try {
                    Main.this.solutionDAO.insert(solution);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }

            @Override
            public void solutionsAdded(Solution[] solutions) {
                try {
                    Main.this.solutionDAO.insert(solutions);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }

            @Override
            public void solutionUpdated(Solution solution) {
                Main.this.sessionManager.updateSolution(solution);

                try {
                    Main.this.solutionDAO.update(solution);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }

            @Override
            public void solutionRemoved(Solution solution) {
                Main.this.sessionManager.removeSolution(solution);

                try {
                    Main.this.solutionDAO.delete(solution);
                } catch (DatabaseException e) {
                    Main.this.messageManager.enqueueMessage(
                            MessageType.ERROR,
                            String.format(_("main.database_error_message"), e.getMessage()));
                }
            }
        });

        // session manager
        this.sessionManager = new SessionManager();
        this.sessionManager.setDailySessionEnabled(Main.this.configurationManager.getConfiguration("DAILYSESSION-ENABLED").equals("TRUE"));
        this.sessionManager.addListener(new SessionManager.Listener() {
            @Override
            public void dailySessionSet(boolean dailySessionEnabled) {
                Main.this.configurationManager.setConfiguration(
                        "DAILYSESSION-ENABLED", dailySessionEnabled ? "TRUE" : "FALSE");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();

                String laf = main.configurationManager.getConfiguration("LOOK-AND-FEEL");
                if (laf == null)
                    laf = UIManager.getSystemLookAndFeelClassName();
                try {
                    UIManager.setLookAndFeel(laf);
                } catch (Exception e) {
                }

                Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/com/puzzletimer/resources/icon.png"));

                // main frame
                MainFrame mainFrame = new MainFrame(
                        main.messageManager,
                        main.configurationManager,
                        main.timerManager,
                        main.puzzleProvider,
                        main.colorManager,
                        main.scrambleParserProvider,
                        main.scramblerProvider,
                        main.tipProvider,
                        main.categoryManager,
                        main.scrambleManager,
                        main.solutionManager,
                        main.sessionManager,
                        main.solutionDAO);
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setIconImage(icon);

                main.categoryManager.setCurrentCategory(main.categoryManager.getCurrentCategory());
                if(main.configurationManager.getConfiguration("DAILYSESSION-ENABLED").equals("TRUE")) {
                    Solution[] sols = main.solutionDAO.getCurrentSession(main.categoryManager.getCurrentCategory());
                    for (Solution solution : sols) {
                        main.sessionManager.addSolution(solution);
                    }
                }

				if(!mainFrame.hasUpdate())
                    mainFrame.setVisible(true);
            }
        });
    }
}
