BEGIN TRANSACTION;

-- scramblers

INSERT INTO SCRAMBLER VALUES
    (0, 'RUBIKS-CUBE-CLL-TRAINING',             'RUBIKS-CUBE', 'CLL training scrambler',               FALSE),
    (0, 'RUBIKS-CUBE-ELL-TRAINING',             'RUBIKS-CUBE', 'ELL training scrambler',               FALSE),
    (0, 'RUBIKS-CUBE-BLD-SINGLE-STICKER-CYCLE', 'RUBIKS-CUBE', 'BLD - Single sticker cycle scrambler', FALSE);


-- tips

CREATE TABLE TIP(
    TIP_ID VARCHAR(128),
    PUZZLE_ID VARCHAR(128),

    PRIMARY KEY(TIP_ID),
    FOREIGN KEY(PUZZLE_ID) REFERENCES PUZZLE(PUZZLE_ID)
);

INSERT INTO TIP VALUES
    ('RUBIKS-CUBE-OPTIMAL-CROSS',            'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-3OP-CYCLES',               'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-CLASSIC-POCHMANN-EDGES',   'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS', 'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-M2-EDGES',                 'RUBIKS-CUBE'),
    ('SQUARE-1-OPTIMAL-CUBE-SHAPE',          'SQUARE-1');


-- category tips

CREATE TABLE CATEGORY_TIPS(
    "ORDER" INTEGER,
    CATEGORY_ID UUID,
    TIP_ID VARCHAR(128),

    PRIMARY KEY(CATEGORY_ID, TIP_ID),
    FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY(CATEGORY_ID) ON DELETE CASCADE,
    FOREIGN KEY(TIP_ID) REFERENCES TIP(TIP_ID)
);

INSERT INTO CATEGORY_TIPS VALUES
    -- Rubik's cube
    (0, '90dea358-e525-4b6c-8b2d-abfa61f02a9d', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- Rubik's cube one-handed
    (0, '3282c6bc-3a7b-4b16-aeae-45ae75b17e47', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- Rubik's cube blindfolded
    (0, '953a7701-6235-4f9b-8dd4-fe32055cb652', 'RUBIKS-CUBE-3OP-CYCLES'),

    -- Rubik's cube with feet
    (0, '761088a1-64fc-47db-92ea-b6c3b812e6f3', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- Square-1
    (0, '748e6c09-cca5-412a-bd92-cc7febed9adf', 'SQUARE-1-OPTIMAL-CUBE-SHAPE');


-- version

UPDATE CONFIGURATION SET VALUE = '0.5' WHERE KEY = 'VERSION';

COMMIT;

