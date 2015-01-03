CREATE TABLE PUZZLE(
    "ORDER" INTEGER,
    PUZZLE_ID VARCHAR(128),
    DESCRIPTION VARCHAR(1024),

    PRIMARY KEY(PUZZLE_ID)
);

INSERT INTO PUZZLE VALUES
    ( 0, '2x2x2-CUBE',   '2x2x2 cube'),
    ( 1, 'RUBIKS-CUBE',  'Rubik''s cube'),
    ( 2, '4x4x4-CUBE',   '4x4x4 cube'),
    ( 3, '5x5x5-CUBE',   '5x5x5 cube'),
    ( 4, '6x6x6-CUBE',   '6x6x6 cube'),
    ( 5, '7x7x7-CUBE',   '7x7x7 cube'),
    ( 6, 'RUBIKS-CLOCK', 'Rubik''s clock'),
    ( 7, 'MEGAMINX',     'Megaminx'),
    ( 8, 'PYRAMINX',     'Pyraminx'),
    ( 9, 'SQUARE-1',     'Square-1'),
    (10, 'OTHER',        'Other');


CREATE TABLE COLOR(
    PUZZLE_ID VARCHAR(128),
    "ORDER" INTEGER,
    FACE_ID VARCHAR(128),
    FACE_DESCRIPTION VARCHAR(1024),
    DEFAULT_R INTEGER,
    DEFAULT_G INTEGER,
    DEFAULT_B INTEGER,
    R INTEGER,
    G INTEGER,
    B INTEGER,

    PRIMARY KEY(PUZZLE_ID, FACE_ID),
    FOREIGN KEY(PUZZLE_ID) REFERENCES PUZZLE(PUZZLE_ID)
);

INSERT INTO COLOR VALUES
    ('2x2x2-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('2x2x2-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('2x2x2-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('2x2x2-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('2x2x2-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('2x2x2-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('RUBIKS-CUBE',   0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('RUBIKS-CUBE',   1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('RUBIKS-CUBE',   2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('RUBIKS-CUBE',   3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('RUBIKS-CUBE',   4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('RUBIKS-CUBE',   5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('4x4x4-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('4x4x4-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('4x4x4-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('4x4x4-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('4x4x4-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('4x4x4-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('5x5x5-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('5x5x5-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('5x5x5-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('5x5x5-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('5x5x5-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('5x5x5-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('6x6x6-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('6x6x6-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('6x6x6-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('6x6x6-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('6x6x6-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('6x6x6-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('7x7x7-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('7x7x7-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('7x7x7-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('7x7x7-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('7x7x7-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('7x7x7-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('RUBIKS-CLOCK',  0, 'FRONT',           'Front',           128, 255, 255, 128, 255, 255),
    ('RUBIKS-CLOCK',  1, 'BACK',            'Back',              0,  16, 204,   0,  16, 204),
    ('RUBIKS-CLOCK',  2, 'HAND-BACKGROUND', 'Hand background', 212,  17,  17, 212,  17,  17),
    ('RUBIKS-CLOCK',  3, 'HAND-FOREGROUND', 'Hand foreground', 255, 234,   0, 255, 234,   0),
    ('RUBIKS-CLOCK',  4, 'PIN-UP',          'Pin up',          255, 234,   0, 255, 234,   0),
    ('RUBIKS-CLOCK',  5, 'PIN-DOWN',        'Pin down',        204, 187,   0, 204, 187,   0),
    ('MEGAMINX',      0, 'FACE-1',          'Face 1',           13, 166, 242,  13, 166, 242),
    ('MEGAMINX',      1, 'FACE-2',          'Face 2',          255,  85,   0, 255,  85,   0),
    ('MEGAMINX',      2, 'FACE-3',          'Face 3',          212,  17,  17, 212,  17,  17),
    ('MEGAMINX',      3, 'FACE-4',          'Face 4',          153,  77,   0, 153,  77,   0),
    ('MEGAMINX',      4, 'FACE-5',          'Face 5',            0, 153,   0,   0, 153,   0),
    ('MEGAMINX',      5, 'FACE-6',          'Face 6',          247, 100, 179, 247, 100, 179),
    ('MEGAMINX',      6, 'FACE-7',          'Face 7',          147,  13, 242, 147,  13, 242),
    ('MEGAMINX',      7, 'FACE-8',          'Face 8',          255, 255, 255, 255, 255, 255),
    ('MEGAMINX',      8, 'FACE-9',          'Face 9',            0, 255,  43,   0, 255,  43),
    ('MEGAMINX',      9, 'FACE-10',         'Face 10',         255, 234,   0, 255, 234,   0),
    ('MEGAMINX',     10, 'FACE-11',         'Face 11',          13, 242, 242,  13, 242, 242),
    ('MEGAMINX',     11, 'FACE-12',         'Face 12',           0,  13, 153,   0,  13, 153),
    ('PYRAMINX',      0, 'FACE-U',          'Face U',            0,  13, 153,   0,  13, 153),
    ('PYRAMINX',      1, 'FACE-R',          'Face R',            0, 153,   0,   0, 153,   0),
    ('PYRAMINX',      2, 'FACE-L',          'Face L',          255, 234,   0, 255, 234,   0),
    ('PYRAMINX',      3, 'FACE-B',          'Face B',          212,  17,  17, 212,  17,  17),
    ('SQUARE-1',      0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('SQUARE-1',      1, 'FACE-D',          'Face D',            0, 153,   0,   0, 153,   0),
    ('SQUARE-1',      2, 'FACE-L',          'Face L',          255, 234,   0, 255, 234,   0),
    ('SQUARE-1',      3, 'FACE-R',          'Face R',            0,  13, 153,   0,  13, 153),
    ('SQUARE-1',      4, 'FACE-F',          'Face F',          255,  85,   0, 255,  85,   0),
    ('SQUARE-1',      5, 'FACE-B',          'Face B',          212,  17,  17, 212,  17,  17);


CREATE TABLE SCRAMBLER(
    "ORDER" INTEGER,
    SCRAMBLER_ID VARCHAR(128),
    PUZZLE_ID VARCHAR(128),
    DESCRIPTION VARCHAR(1024),
    IMPORTER BOOLEAN,

    PRIMARY KEY(SCRAMBLER_ID),
    FOREIGN KEY(PUZZLE_ID) REFERENCES PUZZLE(PUZZLE_ID)
);

INSERT INTO SCRAMBLER VALUES
    (-1, '2x2x2-CUBE-IMPORTER',                          '2x2x2-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '2x2x2-CUBE-RANDOM',                            '2x2x2-CUBE',   'Random scrambler',                             FALSE),
    (-1, 'RUBIKS-CUBE-IMPORTER',                         'RUBIKS-CUBE',  'Importer scrambler',                            TRUE),
    ( 0, 'RUBIKS-CUBE-RANDOM',                           'RUBIKS-CUBE',  'Random scrambler',                             FALSE),
    ( 1, 'RUBIKS-CUBE-FRIDRICH-F2L-TRAINING',            'RUBIKS-CUBE',  'Fridrich - F2L training scrambler',            FALSE),
    ( 2, 'RUBIKS-CUBE-FRIDRICH-OLL-TRAINING',            'RUBIKS-CUBE',  'Fridrich - OLL training scrambler',            FALSE),
    ( 3, 'RUBIKS-CUBE-FRIDRICH-PLL-TRAINING',            'RUBIKS-CUBE',  'Fridrich - PLL training scrambler',            FALSE),
    ( 4, 'RUBIKS-CUBE-3OP-CORNERS-TRAINING',             'RUBIKS-CUBE',  '3OP - Corners training scrambler',             FALSE),
    ( 5, 'RUBIKS-CUBE-3OP-CORNERS-PERMUTATION-TRAINING', 'RUBIKS-CUBE',  '3OP - Corners permutation training scrambler', FALSE),
    ( 6, 'RUBIKS-CUBE-3OP-CORNERS-ORIENTATION-TRAINING', 'RUBIKS-CUBE',  '3OP - Corners orientation training scrambler', FALSE),
    ( 7, 'RUBIKS-CUBE-3OP-EDGES-TRAINING',               'RUBIKS-CUBE',  '3OP - Edges training scrambler',               FALSE),
    ( 8, 'RUBIKS-CUBE-3OP-EDGES-PERMUTATION-TRAINING',   'RUBIKS-CUBE',  '3OP - Edges permutation training scrambler',   FALSE),
    ( 9, 'RUBIKS-CUBE-3OP-EDGES-ORIENTATION-TRAINING',   'RUBIKS-CUBE',  '3OP - Edges orientation training scrambler',   FALSE),
    (10, 'RUBIKS-CUBE-EASY-CROSS',                       'RUBIKS-CUBE',  'Easy cross scrambler',                         FALSE),
    (-1, '4x4x4-CUBE-IMPORTER',                          '4x4x4-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '4x4x4-CUBE-RANDOM',                            '4x4x4-CUBE',   'Random scrambler',                             FALSE),
    (-1, '5x5x5-CUBE-IMPORTER',                          '5x5x5-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '5x5x5-CUBE-RANDOM',                            '5x5x5-CUBE',   'Random scrambler',                             FALSE),
    (-1, '6x6x6-CUBE-IMPORTER',                          '6x6x6-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '6x6x6-CUBE-RANDOM',                            '6x6x6-CUBE',   'Random scrambler',                             FALSE),
    (-1, '7x7x7-CUBE-IMPORTER',                          '7x7x7-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '7x7x7-CUBE-RANDOM',                            '7x7x7-CUBE',   'Random scrambler',                             FALSE),
    (-1, 'RUBIKS-CLOCK-IMPORTER',                        'RUBIKS-CLOCK', 'Importer scrambler',                            TRUE),
    ( 0, 'RUBIKS-CLOCK-RANDOM',                          'RUBIKS-CLOCK', 'Random scrambler',                             FALSE),
    (-1, 'MEGAMINX-IMPORTER',                            'MEGAMINX',     'Importer scrambler',                            TRUE),
    ( 0, 'MEGAMINX-RANDOM',                              'MEGAMINX',     'Random scrambler',                             FALSE),
    (-1, 'PYRAMINX-IMPORTER',                            'PYRAMINX',     'Importer scrambler',                            TRUE),
    ( 0, 'PYRAMINX-RANDOM',                              'PYRAMINX',     'Random scrambler',                             FALSE),
    (-1, 'SQUARE-1-IMPORTER',                            'SQUARE-1',     'Importer scrambler',                            TRUE),
    ( 0, 'SQUARE-1-RANDOM',                              'SQUARE-1',     'Random scrambler',                             FALSE),
    (-1, 'OTHER-IMPORTER',                               'OTHER',        'Importer scrambler',                            TRUE),
    ( 0, 'EMPTY',                                        'OTHER',        'Empty scrambler',                              FALSE);


CREATE TABLE CATEGORY(
    "ORDER" INTEGER,
    CATEGORY_ID UUID,
    SCRAMBLER_ID VARCHAR(128),
    DESCRIPTION VARCHAR(1024),
    USER_DEFINED BOOLEAN,

    PRIMARY KEY(CATEGORY_ID),
    FOREIGN KEY(SCRAMBLER_ID) REFERENCES SCRAMBLER(SCRAMBLER_ID)
);

INSERT INTO CATEGORY VALUES
    (-17, '64b9c16d-dc36-44b4-9605-c93933cdd311', '2x2x2-CUBE-RANDOM',                 '2x2x2 cube',                 FALSE),
    (-16, '90dea358-e525-4b6c-8b2d-abfa61f02a9d', 'RUBIKS-CUBE-RANDOM',                'Rubik''s cube',              FALSE),
    (-15, '3282c6bc-3a7b-4b16-aeae-45ae75b17e47', 'RUBIKS-CUBE-RANDOM',                'Rubik''s cube one-handed',   FALSE),
    (-14, '953a7701-6235-4f9b-8dd4-fe32055cb652', 'RUBIKS-CUBE-RANDOM',                'Rubik''s cube blindfolded',  FALSE),
    (-13, '761088a1-64fc-47db-92ea-b6c3b812e6f3', 'RUBIKS-CUBE-RANDOM',                'Rubik''s cube with feet',    FALSE),
    (-12, '3577f24a-065b-4bcc-9ca3-3df011d07a5d', '4x4x4-CUBE-RANDOM',                 '4x4x4 cube',                 FALSE),
    (-11, '587d884a-b996-4cd6-95bb-c3dafbfae193', '4x4x4-CUBE-RANDOM',                 '4x4x4 blindfolded',          FALSE),
    (-10, 'e3894e40-fb85-497b-a592-c81703901a95', '5x5x5-CUBE-RANDOM',                 '5x5x5 cube',                 FALSE),
    ( -9, '0701c98c-a275-4e51-888c-59dc9de9de1a', '5x5x5-CUBE-RANDOM',                 '5x5x5 blindfolded',          FALSE),
    ( -8, '86227762-6249-4417-840b-3c8ba7b0bd33', '6x6x6-CUBE-RANDOM',                 '6x6x6 cube',                 FALSE),
    ( -7, 'b9375ece-5a31-4dc4-b58e-ecb8a638e102', '7x7x7-CUBE-RANDOM',                 '7x7x7 cube',                 FALSE),
    ( -6, '7f244648-0e14-44cd-8399-b41ccdb6d7db', 'RUBIKS-CLOCK-RANDOM',               'Rubik''s clock',             FALSE),
    ( -5, 'c50f60c8-99d2-48f4-8502-d110a0ef2fc9', 'MEGAMINX-RANDOM',                   'Megaminx',                   FALSE),
    ( -4, '6750cbfd-542d-42b7-9cf4-56265549dd88', 'PYRAMINX-RANDOM',                   'Pyraminx',                   FALSE),
    ( -3, '748e6c09-cca5-412a-bd92-cc7febed9adf', 'SQUARE-1-RANDOM',                   'Square-1',                   FALSE),
    ( -2, '1a647910-41ff-48d1-b9f5-6f1874da9265', 'EMPTY',                             'Rubik''s magic',             FALSE),
    ( -1, 'f8f96514-bcb8-4f46-abb5-aecb7da4e4de', 'EMPTY',                             'Master magic',               FALSE),
    (  0, 'ccc635c7-9d5d-4920-96ad-4acd87549334', 'RUBIKS-CUBE-EASY-CROSS',            'Rubik''s cube - Easy cross',  TRUE),
    (  0, '98102dd7-8216-40a6-8864-1bb61afed415', 'RUBIKS-CUBE-FRIDRICH-F2L-TRAINING', 'Fridrich - F2L training',     TRUE),
    (  0, '01e76746-c97a-489a-a80d-84cd18b6d9e2', 'RUBIKS-CUBE-FRIDRICH-OLL-TRAINING', 'Fridrich - OLL training',     TRUE),
    (  0, '37eca744-116a-433f-a791-912a59efbad5', 'RUBIKS-CUBE-FRIDRICH-PLL-TRAINING', 'Fridrich - PLL training',     TRUE),
    (  0, '5567fdeb-b0a6-43a9-84d6-66f4274c0c7a', 'RUBIKS-CUBE-3OP-CORNERS-TRAINING',  '3OP - Corners training',      TRUE),
    (  0, 'c6e2d33b-0397-440f-bcb0-72d814d72210', 'RUBIKS-CUBE-3OP-EDGES-TRAINING',    '3OP - Edges training',        TRUE);

CREATE TABLE SOLUTION(
    SOLUTION_ID UUID,
    CATEGORY_ID UUID,
    SCRAMBLER_ID VARCHAR(128),
    SEQUENCE VARCHAR(4096),
    START TIMESTAMP,
    END TIMESTAMP,
    PENALTY VARCHAR(16),

    PRIMARY KEY(SOLUTION_ID),
    FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY(CATEGORY_ID) ON DELETE CASCADE,
    FOREIGN KEY(SCRAMBLER_ID) REFERENCES SCRAMBLER(SCRAMBLER_ID) ON DELETE CASCADE
);


CREATE TABLE CONFIGURATION(
    KEY VARCHAR(128),
    VALUE VARCHAR(1024),

    PRIMARY KEY(KEY)
);

INSERT INTO CONFIGURATION VALUES
    ('VERSION',                     '0.3'),
    ('CURRENT-CATEGORY',            '90dea358-e525-4b6c-8b2d-abfa61f02a9d'),
    ('TIMER-TRIGGER',               'KEYBOARD-TIMER-SPACE'),
    ('STACKMAT-TIMER-INPUT-DEVICE', '');

