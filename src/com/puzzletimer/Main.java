package com.puzzletimer;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.puzzletimer.database.CategoryDAO;
import com.puzzletimer.database.ColorDAO;
import com.puzzletimer.database.ConfigurationDAO;
import com.puzzletimer.database.SolutionDAO;
import com.puzzletimer.gui.MainFrame;
import com.puzzletimer.models.Category;
import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.ConfigurationEntry;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.puzzles.PuzzleProvider;
import com.puzzletimer.scramblers.ScramblerProvider;
import com.puzzletimer.state.CategoryListener;
import com.puzzletimer.state.CategoryManager;
import com.puzzletimer.state.ColorListener;
import com.puzzletimer.state.ColorManager;
import com.puzzletimer.state.ConfigurationListener;
import com.puzzletimer.state.ConfigurationManager;
import com.puzzletimer.state.ScrambleManager;
import com.puzzletimer.state.SessionManager;
import com.puzzletimer.state.SolutionListener;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.state.TimerListener;
import com.puzzletimer.state.TimerManager;
import com.puzzletimer.timer.Timer;

public class Main {
    private ConfigurationDAO configurationDAO;
    private ColorDAO colorDAO;
    private CategoryDAO categoryDAO;
    private SolutionDAO solutionDAO;

    private ConfigurationManager configurationManager;
    private TimerManager timerManager;
    private PuzzleProvider puzzleProvider;
    private ColorManager colorManager;
    private ScrambleParserProvider scrambleParserProvider;
    private ScramblerProvider scramblerProvider;
    private CategoryManager categoryManager;
    private ScrambleManager scrambleManager;
    private SolutionManager solutionManager;
    private SessionManager sessionManager;

    public Main() {
        // make empty database if necessary
        try {
            File databaseFile = new File("puzzletimer.h2.db");
            if (!databaseFile.exists()) {
                BufferedInputStream input = new BufferedInputStream(getClass().getResourceAsStream("/com/puzzletimer/resources/puzzletimer.h2.db"));
                FileOutputStream output = new FileOutputStream("puzzletimer.h2.db");

                for (;;) {
                    int data = input.read();
                    if (data < 0) {
                        break;
                    }

                    output.write(data);
                }

                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
            // TODO: show error message and quit
        }

        // connect to database
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:puzzletimer;IFEXISTS=TRUE", "sa", "");
        } catch (Exception e) {
            e.printStackTrace();
            return;
            // TODO: show error message and quit
        }

        // configuration DAO
        this.configurationDAO = new ConfigurationDAO(connection);

        // configuration manager
        this.configurationManager = new ConfigurationManager(this.configurationDAO.getAll());
        this.configurationManager.addConfigurationListener(new ConfigurationListener() {
            @Override
            public void configurationEntryUpdated(String key, String value) {
                Main.this.configurationDAO.update(
                    new ConfigurationEntry(key, value));
            }
        });

        // timer manager
        this.timerManager = new TimerManager();
        this.timerManager.addTimerListener(new TimerListener() {
            @Override
            public void timerStopped(Timing timing) {
                Main.this.solutionManager.addSolution(
                    new Solution(
                        UUID.randomUUID(),
                        Main.this.categoryManager.getCurrentCategory().getCategoryId(),
                        Main.this.scrambleManager.getCurrentScramble(),
                        timing,
                        ""));
                Main.this.scrambleManager.changeScramble();
            }

            @Override
            public void timerChanged(Timer timer) {
                Main.this.configurationManager.setConfiguration(
                    "TIMER-TRIGGER", timer.getTimerId());
            }
        });

        // puzzle provider
        this.puzzleProvider = new PuzzleProvider();

        // color DAO
        this.colorDAO = new ColorDAO(connection);

        // color manager
        this.colorManager = new ColorManager(this.colorDAO.getAll());
        this.colorManager.addColorListener(new ColorListener() {
            @Override
            public void colorSchemeUpdated(ColorScheme colorScheme) {
                Main.this.colorDAO.update(colorScheme);
            }
        });

        // scramble parser provider
        this.scrambleParserProvider = new ScrambleParserProvider();

        // scrambler provider
        this.scramblerProvider = new ScramblerProvider();

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
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void currentCategoryChanged(Category category) {
                Main.this.configurationManager.setConfiguration(
                    "CURRENT-CATEGORY",
                    category.getCategoryId().toString());
                Main.this.solutionManager.loadSolutions(Main.this.solutionDAO.getAll(category));
                Main.this.sessionManager.clearSession();
            }

            @Override
            public void categoryAdded(Category category) {
                Main.this.categoryDAO.insert(category);
            }

            @Override
            public void categoryRemoved(Category category) {
                Main.this.categoryDAO.delete(category);
            }

            @Override
            public void categoryUpdated(Category category) {
                Main.this.categoryDAO.update(category);
            }
        });

        // scramble manager
        this.scrambleManager = new ScrambleManager(
            this.scramblerProvider,
            this.scramblerProvider.get(currentCategory.getScramblerId()));
        this.categoryManager.addCategoryListener(new CategoryListener() {
            @Override
            public void currentCategoryChanged(Category category) {
                Main.this.scrambleManager.setCategory(category);
            }
        });

        // solution DAO
        this.solutionDAO = new SolutionDAO(connection, this.scramblerProvider, this.scrambleParserProvider);

        // solution manager
        this.solutionManager = new SolutionManager();
        this.solutionManager.addSolutionListener(new SolutionListener() {
            @Override
            public void solutionAdded(Solution solution) {
                Main.this.sessionManager.addSolution(solution);
                Main.this.solutionDAO.insert(solution);
            }

            @Override
            public void solutionUpdated(Solution solution) {
                Main.this.sessionManager.updateSolution(solution);
                Main.this.solutionDAO.update(solution);
            }

            @Override
            public void solutionRemoved(Solution solution) {
                Main.this.sessionManager.removeSolution(solution);
                Main.this.solutionDAO.delete(solution);
            }
        });

        // session manager
        this.sessionManager = new SessionManager();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e){
                }

                Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/com/puzzletimer/resources/icon.png"));

                // main frame
                MainFrame mainFrame = new MainFrame(
                    main.configurationManager,
                    main.timerManager,
                    main.puzzleProvider,
                    main.colorManager,
                    main.scrambleParserProvider,
                    main.scramblerProvider,
                    main.categoryManager,
                    main.scrambleManager,
                    main.solutionManager,
                    main.sessionManager);
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setIconImage(icon);

                main.categoryManager.setCurrentCategory(main.categoryManager.getCurrentCategory());

                mainFrame.setVisible(true);
            }
        });
    }
}
