BEGIN TRANSACTION;

-- puzzles

INSERT INTO PUZZLE VALUES
    ( 6, '8x8x8-CUBE',   '8x8x8 cube'),
    ( 7, '9x9x9-CUBE',   '9x9x9 cube');

UPDATE PUZZLE SET "ORDER" =  8 WHERE PUZZLE_ID = 'RUBIKS-CLOCK';
UPDATE PUZZLE SET "ORDER" =  9 WHERE PUZZLE_ID = 'MEGAMINX';
UPDATE PUZZLE SET "ORDER" = 10 WHERE PUZZLE_ID = 'PYRAMINX';
UPDATE PUZZLE SET "ORDER" = 11 WHERE PUZZLE_ID = 'SQUARE-1';
UPDATE PUZZLE SET "ORDER" = 12 WHERE PUZZLE_ID = 'SKEWB';
UPDATE PUZZLE SET "ORDER" = 13 WHERE PUZZLE_ID = 'FLOPPY-CUBE';
UPDATE PUZZLE SET "ORDER" = 14 WHERE PUZZLE_ID = 'TOWER-CUBE';
UPDATE PUZZLE SET "ORDER" = 15 WHERE PUZZLE_ID = 'RUBIKS-TOWER';
UPDATE PUZZLE SET "ORDER" = 16 WHERE PUZZLE_ID = 'RUBIKS-DOMINO';
UPDATE PUZZLE SET "ORDER" = 17 WHERE PUZZLE_ID = 'OTHER';

-- scramblers

INSERT INTO SCRAMBLER VALUES
    (-1, '8x8x8-CUBE-IMPORTER',                          '8x8x8-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '8x8x8-CUBE-RANDOM',                            '8x8x8-CUBE',   'Random scrambler',                             FALSE),
    (-1, '9x9x9-CUBE-IMPORTER',                          '9x9x9-CUBE',   'Importer scrambler',                            TRUE),
    ( 0, '9x9x9-CUBE-RANDOM',                            '9x9x9-CUBE',   'Random scrambler',                             FALSE);

-- colors

INSERT INTO COLOR VALUES
    ('8x8x8-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('8x8x8-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('8x8x8-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('8x8x8-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('8x8x8-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('8x8x8-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153),
    ('9x9x9-CUBE',    0, 'FACE-U',          'Face U',          255, 255, 255, 255, 255, 255),
    ('9x9x9-CUBE',    1, 'FACE-D',          'Face D',          255, 234,   0, 255, 234,   0),
    ('9x9x9-CUBE',    2, 'FACE-L',          'Face L',          255,  85,   0, 255,  85,   0),
    ('9x9x9-CUBE',    3, 'FACE-R',          'Face R',          212,  17,  17, 212,  17,  17),
    ('9x9x9-CUBE',    4, 'FACE-F',          'Face F',            0, 153,   0,   0, 153,   0),
    ('9x9x9-CUBE',    5, 'FACE-B',          'Face B',            0,  13, 153,   0,  13, 153);

-- categories

INSERT INTO CATEGORY VALUES
    (-1, '7adb4689-1961-43e4-9063-c42f578fde34', 'SKEWB-RANDOM', 'Skewb', FALSE);

UPDATE CATEGORY SET "ORDER" = -16 WHERE CATEGORY_ID = '64b9c16d-dc36-44b4-9605-c93933cdd311';
UPDATE CATEGORY SET "ORDER" = -15 WHERE CATEGORY_ID = '90dea358-e525-4b6c-8b2d-abfa61f02a9d';
UPDATE CATEGORY SET "ORDER" = -14 WHERE CATEGORY_ID = '3282c6bc-3a7b-4b16-aeae-45ae75b17e47';
UPDATE CATEGORY SET "ORDER" = -13 WHERE CATEGORY_ID = '953a7701-6235-4f9b-8dd4-fe32055cb652';
UPDATE CATEGORY SET "ORDER" = -12 WHERE CATEGORY_ID = '761088a1-64fc-47db-92ea-b6c3b812e6f3';
UPDATE CATEGORY SET "ORDER" = -11 WHERE CATEGORY_ID = '3577f24a-065b-4bcc-9ca3-3df011d07a5d';
UPDATE CATEGORY SET "ORDER" = -10 WHERE CATEGORY_ID = '587d884a-b996-4cd6-95bb-c3dafbfae193';
UPDATE CATEGORY SET "ORDER" =  -9 WHERE CATEGORY_ID = 'e3894e40-fb85-497b-a592-c81703901a95';
UPDATE CATEGORY SET "ORDER" =  -8 WHERE CATEGORY_ID = '0701c98c-a275-4e51-888c-59dc9de9de1a';
UPDATE CATEGORY SET "ORDER" =  -7 WHERE CATEGORY_ID = '86227762-6249-4417-840b-3c8ba7b0bd33';
UPDATE CATEGORY SET "ORDER" =  -6 WHERE CATEGORY_ID = 'b9375ece-5a31-4dc4-b58e-ecb8a638e102';
UPDATE CATEGORY SET "ORDER" =  -5 WHERE CATEGORY_ID = '7f244648-0e14-44cd-8399-b41ccdb6d7db';
UPDATE CATEGORY SET "ORDER" =  -4 WHERE CATEGORY_ID = 'c50f60c8-99d2-48f4-8502-d110a0ef2fc9';
UPDATE CATEGORY SET "ORDER" =  -3 WHERE CATEGORY_ID = '6750cbfd-542d-42b7-9cf4-56265549dd88';
UPDATE CATEGORY SET "ORDER" =  -2 WHERE CATEGORY_ID = '748e6c09-cca5-412a-bd92-cc7febed9adf';
UPDATE CATEGORY SET "ORDER" =   0 WHERE CATEGORY_ID = '1a647910-41ff-48d1-b9f5-6f1874da9265';
UPDATE CATEGORY SET "ORDER" =   0 WHERE CATEGORY_ID = 'f8f96514-bcb8-4f46-abb5-aecb7da4e4de';
UPDATE CATEGORY SET "USER_DEFINED" = TRUE WHERE CATEGORY_ID = '1a647910-41ff-48d1-b9f5-6f1874da9265';
UPDATE CATEGORY SET "USER_DEFINED" = TRUE WHERE CATEGORY_ID = 'f8f96514-bcb8-4f46-abb5-aecb7da4e4de';

-- configuration

INSERT INTO CONFIGURATION VALUES
    ('TIMER-PRECISION', 'MILLISECONDS'),
    ('SMOOTH-TIMING-ENABLED', 'TRUE');

-- version

UPDATE CONFIGURATION SET VALUE = '0.6' WHERE KEY = 'VERSION';

COMMIT;