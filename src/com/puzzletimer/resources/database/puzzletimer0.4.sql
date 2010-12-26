BEGIN TRANSACTION;

-- puzzles

INSERT INTO PUZZLE VALUES
    (10, 'SKEWB',         'Skewb'),
    (11, 'FLOPPY-CUBE',   'Floppy cube'),
    (12, 'TOWER-CUBE',    'Tower cube'),
    (13, 'RUBIKS-TOWER',  'Rubik''s tower'),
    (14, 'RUBIKS-DOMINO', 'Rubik''s domino');

UPDATE PUZZLE SET "ORDER" = 15 WHERE PUZZLE_ID = 'OTHER';


-- colors

INSERT INTO COLOR VALUES
    ('SKEWB',         0, 'FACE-U', 'Face U', 255, 255, 255, 255, 255, 255),
    ('SKEWB',         1, 'FACE-D', 'Face D', 255, 234,   0, 255, 234,   0),
    ('SKEWB',         2, 'FACE-L', 'Face L', 255,  85,   0, 255,  85,   0),
    ('SKEWB',         3, 'FACE-R', 'Face R', 212,  17,  17, 212,  17,  17),
    ('SKEWB',         4, 'FACE-F', 'Face F',   0, 153,   0,   0, 153,   0),
    ('SKEWB',         5, 'FACE-B', 'Face B',   0,  13, 153,   0,  13, 153),
    ('FLOPPY-CUBE',   0, 'FACE-U', 'Face U', 200, 204, 200, 200, 204, 200),
    ('FLOPPY-CUBE',   1, 'FACE-D', 'Face D',  75,  77,  75,  75,  77,  75),
    ('FLOPPY-CUBE',   2, 'FACE-L', 'Face L', 153, 255, 153, 153, 255, 153),
    ('FLOPPY-CUBE',   3, 'FACE-R', 'Face R',  51, 185, 255,  51, 185, 255),
    ('FLOPPY-CUBE',   4, 'FACE-F', 'Face F', 255,   0,  38, 255,   0,  38),
    ('FLOPPY-CUBE',   5, 'FACE-B', 'Face B', 255, 239,  51, 255, 239,  51),
    ('TOWER-CUBE',    0, 'FACE-U', 'Face U', 200, 204, 200, 200, 204, 200),
    ('TOWER-CUBE',    1, 'FACE-D', 'Face D',  75,  77,  75,  75,  77,  75),
    ('TOWER-CUBE',    2, 'FACE-L', 'Face L', 153, 255, 153, 153, 255, 153),
    ('TOWER-CUBE',    3, 'FACE-R', 'Face R',  51, 185, 255,  51, 185, 255),
    ('TOWER-CUBE',    4, 'FACE-F', 'Face F', 255,   0,  38, 255,   0,  38),
    ('TOWER-CUBE',    5, 'FACE-B', 'Face B', 255, 239,  51, 255, 239,  51),
    ('RUBIKS-TOWER',  0, 'FACE-U', 'Face U', 255, 255, 255, 255, 255, 255),
    ('RUBIKS-TOWER',  5, 'FACE-D', 'Face D',   0,  13, 153,   0,  13, 153),
    ('RUBIKS-TOWER',  2, 'FACE-L', 'Face L', 255,  85,   0, 255,  85,   0),
    ('RUBIKS-TOWER',  3, 'FACE-R', 'Face R', 212,  17,  17, 212,  17,  17),
    ('RUBIKS-TOWER',  4, 'FACE-F', 'Face F',   0, 153,   0,   0, 153,   0),
    ('RUBIKS-TOWER',  1, 'FACE-B', 'Face B', 255, 234,   0, 255, 234,   0),
    ('RUBIKS-DOMINO', 0, 'FACE-U', 'Face U', 255, 255, 255, 255, 255, 255),
    ('RUBIKS-DOMINO', 1, 'FACE-D', 'Face D', 255, 234,   0, 255, 234,   0),
    ('RUBIKS-DOMINO', 2, 'FACE-L', 'Face L', 255,  85,   0, 255,  85,   0),
    ('RUBIKS-DOMINO', 3, 'FACE-R', 'Face R', 212,  17,  17, 212,  17,  17),
    ('RUBIKS-DOMINO', 4, 'FACE-F', 'Face F',   0, 153,   0,   0, 153,   0),
    ('RUBIKS-DOMINO', 5, 'FACE-B', 'Face B',   0,  13, 153,   0,  13, 153);


-- scramblers

INSERT INTO SCRAMBLER VALUES
    ( 1, '2x2x2-CUBE-URF',                       '2x2x2-CUBE',    '<U, R, F> scrambler',                  FALSE),
    ( 2, '2x2x2-CUBE-SUBOPTIMAL-URF',            '2x2x2-CUBE',    'Suboptimal <U, R, F> scrambler',       FALSE),
    ( 1, 'RUBIKS-CUBE-LU',                       'RUBIKS-CUBE',   '<L, U> scrambler',                     FALSE),
    ( 2, 'RUBIKS-CUBE-RU',                       'RUBIKS-CUBE',   '<R, U> scrambler',                     FALSE),
    (12, 'RUBIKS-CUBE-3OP-ORIENTATION-TRAINING', 'RUBIKS-CUBE',   '3OP - Orientation training scrambler', FALSE),
    (13, 'RUBIKS-CUBE-3OP-PERMUTATION-TRAINING', 'RUBIKS-CUBE',   '3OP - Permutation training scrambler', FALSE),
    ( 1, 'PYRAMINX-SUBOPTIMAL-RANDOM',           'PYRAMINX',      'Suboptimal random scrambler',          FALSE),
    ( 1, 'SQUARE-1-CUBE-SHAPE',                  'SQUARE-1',      'Cube shape scrambler',                 FALSE),
    (-1, 'SKEWB-IMPORTER',                       'SKEWB',         'Importer scrambler',                    TRUE),
    ( 0, 'SKEWB-RANDOM',                         'SKEWB',         'Random scrambler',                     FALSE),
    (-1, 'FLOPPY-CUBE-IMPORTER',                 'FLOPPY-CUBE',   'Importer scrambler',                    TRUE),
    ( 0, 'FLOPPY-CUBE-RANDOM',                   'FLOPPY-CUBE',   'Random scrambler',                     FALSE),
    (-1, 'TOWER-CUBE-IMPORTER',                  'TOWER-CUBE',    'Importer scrambler',                    TRUE),
    ( 0, 'TOWER-CUBE-RANDOM',                    'TOWER-CUBE',    'Random scrambler',                     FALSE),
    (-1, 'RUBIKS-TOWER-IMPORTER',                'RUBIKS-TOWER',  'Importer scrambler',                    TRUE),
    ( 0, 'RUBIKS-TOWER-RANDOM',                  'RUBIKS-TOWER',  'Random scrambler',                     FALSE),
    (-1, 'RUBIKS-DOMINO-IMPORTER',               'RUBIKS-DOMINO', 'Importer scrambler',                    TRUE),
    ( 0, 'RUBIKS-DOMINO-RANDOM',                 'RUBIKS-DOMINO', 'Random scrambler',                     FALSE);

UPDATE SCRAMBLER SET "ORDER" =  3 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-FRIDRICH-F2L-TRAINING';
UPDATE SCRAMBLER SET "ORDER" =  4 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-FRIDRICH-OLL-TRAINING';
UPDATE SCRAMBLER SET "ORDER" =  5 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-FRIDRICH-PLL-TRAINING';
UPDATE SCRAMBLER SET "ORDER" =  6 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-3OP-CORNERS-TRAINING';
UPDATE SCRAMBLER SET "ORDER" =  7 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-3OP-CORNERS-PERMUTATION-TRAINING';
UPDATE SCRAMBLER SET "ORDER" =  8 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-3OP-CORNERS-ORIENTATION-TRAINING';
UPDATE SCRAMBLER SET "ORDER" =  9 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-3OP-EDGES-TRAINING';
UPDATE SCRAMBLER SET "ORDER" = 10 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-3OP-EDGES-PERMUTATION-TRAINING';
UPDATE SCRAMBLER SET "ORDER" = 11 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-3OP-EDGES-ORIENTATION-TRAINING';
UPDATE SCRAMBLER SET "ORDER" = 14 WHERE SCRAMBLER_ID = 'RUBIKS-CUBE-EASY-CROSS';


-- categories

UPDATE CATEGORY SET SCRAMBLER_ID = '2x2x2-CUBE-URF' WHERE CATEGORY_ID = '64b9c16d-dc36-44b4-9605-c93933cdd311';


-- configuration

INSERT INTO CONFIGURATION VALUES
    ('INSPECTION-TIME-ENABLED', 'FALSE');

UPDATE CONFIGURATION SET VALUE = '0.4' WHERE KEY = 'VERSION';

COMMIT

