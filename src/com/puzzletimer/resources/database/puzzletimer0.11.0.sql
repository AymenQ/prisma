BEGIN TRANSACTION;

UPDATE CONFIGURATION SET VALUE = '0.11.0' WHERE KEY = 'VERSION';

INSERT INTO SCRAMBLER VALUES
  (0, '2x2x2-CUBE-WCA', '2x2x2-CUBE', 'WCA scrambler', FALSE),
  (0, 'RUBIKS-CUBE-WCA', 'RUBIKS-CUBE', 'WCA scrambler', FALSE),
  (0, '4x4x4-CUBE-WCA', '4x4x4-CUBE', 'WCA scrambler', FALSE),
  (0, '5x5x5-CUBE-WCA', '5x5x5-CUBE', 'WCA scrambler', FALSE),
  (0, '6x6x6-CUBE-WCA', '6x6x6-CUBE', 'WCA scrambler', FALSE),
  (0, '7x7x7-CUBE-WCA', '7x7x7-CUBE', 'WCA scrambler', FALSE),
  (0, 'RUBIKS-CLOCK-WCA', 'RUBIKS-CLOCK', 'WCA scrambler', FALSE),
  (0, 'MEGAMINX-WCA', 'MEGAMINX', 'WCA scrambler', FALSE),
  (0, 'PYRAMINX-WCA', 'PYRAMINX', 'WCA scrambler', FALSE),
  (0, 'SQUARE-1-WCA', 'SQUARE-1', 'WCA scrambler', FALSE),
  (0, 'SKEWB-WCA', 'SKEWB', 'WCA scrambler', FALSE);

COMMIT;