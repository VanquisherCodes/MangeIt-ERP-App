USE a25pt201;

-- Check that the target groups exist before seeding historical ML data.
SELECT group_id, group_name
FROM StudentGroup
WHERE group_name IN ('AI Research Group', 'Debate Society', 'Robotics Club');

-- Separate historical table for ML training seed data.
-- This keeps synthetic / backfilled rows out of the live operational tables.
CREATE TABLE IF NOT EXISTS MlTaskCostHistory (
    history_id INT NOT NULL AUTO_INCREMENT,
    group_id INT NOT NULL,
    task_name VARCHAR(150) NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    estimated_hours DECIMAL(8,2) NOT NULL,
    team_size INT NOT NULL,
    allocated_amount DECIMAL(12,2) NOT NULL,
    actual_cost DECIMAL(12,2) NOT NULL,
    completion_status VARCHAR(20) NOT NULL DEFAULT 'completed',
    task_completed_at DATE NOT NULL,
    source_type VARCHAR(20) NOT NULL DEFAULT 'historical_seed',
    source_label VARCHAR(100) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (history_id),
    KEY idx_ml_history_group_date (group_id, task_completed_at),
    KEY idx_ml_history_category (category_name),
    CONSTRAINT fk_ml_history_group
        FOREIGN KEY (group_id) REFERENCES StudentGroup(group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_ml_history_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT chk_ml_history_amounts
        CHECK (allocated_amount >= 0 AND actual_cost >= 0),
    CONSTRAINT chk_ml_history_team_size
        CHECK (team_size >= 1),
    CONSTRAINT chk_ml_history_estimated_hours
        CHECK (estimated_hours >= 0),
    CONSTRAINT chk_ml_history_status
        CHECK (completion_status IN ('completed', 'cancelled'))
);

-- Optional cleanup if you want to rerun the exact same historical seed set.
DELETE FROM MlTaskCostHistory
WHERE source_type = 'historical_seed'
  AND source_label = 'seed_v1';

-- Seed 3 years of historical training rows for the groups currently in the app.
INSERT INTO MlTaskCostHistory (
    group_id,
    task_name,
    category_name,
    priority,
    estimated_hours,
    team_size,
    allocated_amount,
    actual_cost,
    completion_status,
    task_completed_at,
    source_type,
    source_label
)
SELECT
    sg.group_id,
    seed.task_name,
    seed.category_name,
    seed.priority,
    seed.estimated_hours,
    seed.team_size,
    seed.allocated_amount,
    seed.actual_cost,
    seed.completion_status,
    seed.task_completed_at,
    'historical_seed',
    'seed_v1'
FROM StudentGroup sg
JOIN (
    SELECT 'AI Research Group' AS group_name, 'Intro ML Workshop' AS task_name, 'events' AS category_name, 'HIGH' AS priority, 18.00 AS estimated_hours, 4 AS team_size, 420.00 AS allocated_amount, 395.00 AS actual_cost, 'completed' AS completion_status, '2023-03-15' AS task_completed_at
    UNION ALL SELECT 'AI Research Group', 'GPU Cloud Credits', 'equipment', 'HIGH', 8.00, 2, 320.00, 338.00, 'completed', '2023-05-09'
    UNION ALL SELECT 'AI Research Group', 'Research Poster Campaign', 'marketing', 'MEDIUM', 6.00, 3, 110.00, 96.00, 'completed', '2023-10-04'
    UNION ALL SELECT 'AI Research Group', 'Conference Travel Support', 'travel', 'HIGH', 10.00, 2, 560.00, 590.00, 'completed', '2023-11-21'
    UNION ALL SELECT 'AI Research Group', 'Campus LLM Workshop', 'events', 'HIGH', 20.00, 5, 460.00, 445.00, 'completed', '2024-02-28'
    UNION ALL SELECT 'AI Research Group', 'Dataset License Renewal', 'equipment', 'MEDIUM', 7.00, 2, 280.00, 265.00, 'completed', '2024-05-16'
    UNION ALL SELECT 'AI Research Group', 'Recruitment Social Media Pack', 'marketing', 'LOW', 5.00, 2, 95.00, 88.00, 'completed', '2024-09-12'
    UNION ALL SELECT 'AI Research Group', 'Hackathon Travel Pool', 'travel', 'MEDIUM', 9.00, 4, 610.00, 635.00, 'completed', '2024-11-06'
    UNION ALL SELECT 'AI Research Group', 'Applied AI Demo Night', 'events', 'HIGH', 22.00, 6, 520.00, 505.00, 'completed', '2025-03-20'
    UNION ALL SELECT 'AI Research Group', 'Vision Sensor Toolkit', 'equipment', 'MEDIUM', 11.00, 3, 360.00, 372.00, 'completed', '2025-05-14'
    UNION ALL SELECT 'AI Research Group', 'Student Outreach Flyers', 'marketing', 'LOW', 4.00, 2, 85.00, 78.00, 'completed', '2025-09-19'
    UNION ALL SELECT 'AI Research Group', 'Research Showcase Travel', 'travel', 'HIGH', 12.00, 3, 640.00, 618.00, 'completed', '2025-11-08'

    UNION ALL SELECT 'Debate Society', 'Freshers Debate Night', 'events', 'HIGH', 14.00, 5, 350.00, 330.00, 'completed', '2023-02-17'
    UNION ALL SELECT 'Debate Society', 'Portable Audio Set', 'equipment', 'MEDIUM', 6.00, 2, 240.00, 252.00, 'completed', '2023-04-25'
    UNION ALL SELECT 'Debate Society', 'Regional Tournament Travel', 'travel', 'HIGH', 9.00, 4, 480.00, 510.00, 'completed', '2023-08-30'
    UNION ALL SELECT 'Debate Society', 'Open Day Posters', 'marketing', 'LOW', 3.00, 2, 70.00, 64.00, 'completed', '2023-10-11'
    UNION ALL SELECT 'Debate Society', 'Interfaculty Debate Finals', 'events', 'HIGH', 16.00, 6, 390.00, 408.00, 'completed', '2024-03-09'
    UNION ALL SELECT 'Debate Society', 'Wireless Mic Replacement', 'equipment', 'MEDIUM', 5.00, 2, 215.00, 205.00, 'completed', '2024-05-02'
    UNION ALL SELECT 'Debate Society', 'National Debate Trip', 'travel', 'HIGH', 11.00, 5, 540.00, 558.00, 'completed', '2024-09-18'
    UNION ALL SELECT 'Debate Society', 'Membership Recruitment Cards', 'marketing', 'LOW', 4.00, 2, 92.00, 86.00, 'completed', '2024-10-27'
    UNION ALL SELECT 'Debate Society', 'Spring Public Speaking Event', 'events', 'HIGH', 18.00, 6, 430.00, 418.00, 'completed', '2025-02-23'
    UNION ALL SELECT 'Debate Society', 'Timing Bell and Podium Fix', 'equipment', 'LOW', 4.00, 1, 125.00, 119.00, 'completed', '2025-04-16'
    UNION ALL SELECT 'Debate Society', 'University League Travel', 'travel', 'MEDIUM', 10.00, 4, 500.00, 492.00, 'completed', '2025-09-06'
    UNION ALL SELECT 'Debate Society', 'Debate Society Banner Print', 'marketing', 'LOW', 3.50, 2, 88.00, 91.00, 'completed', '2025-11-14'

    UNION ALL SELECT 'Robotics Club', 'Line Follower Workshop', 'events', 'HIGH', 17.00, 5, 410.00, 392.00, 'completed', '2023-03-08'
    UNION ALL SELECT 'Robotics Club', 'Servo Motor Starter Pack', 'equipment', 'HIGH', 8.00, 3, 340.00, 352.00, 'completed', '2023-05-27'
    UNION ALL SELECT 'Robotics Club', 'Competition Travel Support', 'travel', 'HIGH', 12.00, 4, 620.00, 645.00, 'completed', '2023-09-13'
    UNION ALL SELECT 'Robotics Club', 'Sponsor Flyer Printing', 'marketing', 'LOW', 4.00, 2, 95.00, 89.00, 'completed', '2023-10-22'
    UNION ALL SELECT 'Robotics Club', 'Autonomous Robots Demo Day', 'events', 'HIGH', 19.00, 6, 455.00, 470.00, 'completed', '2024-02-14'
    UNION ALL SELECT 'Robotics Club', 'Sensor Bundle Purchase', 'equipment', 'MEDIUM', 9.00, 3, 310.00, 298.00, 'completed', '2024-06-05'
    UNION ALL SELECT 'Robotics Club', 'Regional Competition Trip', 'travel', 'HIGH', 13.00, 5, 670.00, 692.00, 'completed', '2024-09-26'
    UNION ALL SELECT 'Robotics Club', 'Outreach Poster Set', 'marketing', 'LOW', 4.00, 2, 80.00, 76.00, 'completed', '2024-11-02'
    UNION ALL SELECT 'Robotics Club', 'Campus Robotics Expo', 'events', 'HIGH', 21.00, 6, 500.00, 482.00, 'completed', '2025-03-11'
    UNION ALL SELECT 'Robotics Club', 'Microcontroller Refill', 'equipment', 'MEDIUM', 7.00, 2, 255.00, 262.00, 'completed', '2025-05-29'
    UNION ALL SELECT 'Robotics Club', 'Nationals Travel Kit', 'travel', 'HIGH', 14.00, 5, 710.00, 734.00, 'completed', '2025-09-15'
    UNION ALL SELECT 'Robotics Club', 'Demo Booth Promo Cards', 'marketing', 'LOW', 3.50, 2, 78.00, 73.00, 'completed', '2025-10-30'
) seed ON seed.group_name = sg.group_name;

-- View used later by Python training scripts.
CREATE OR REPLACE VIEW MlTaskCostTrainingView AS
SELECT
    history_id,
    group_id,
    task_name,
    category_name,
    priority,
    estimated_hours,
    team_size,
    allocated_amount,
    actual_cost,
    actual_cost - allocated_amount AS variance,
    completion_status,
    task_completed_at,
    source_type,
    source_label
FROM MlTaskCostHistory
WHERE completion_status = 'completed';

-- Quick verification queries.
SELECT sg.group_name, COUNT(*) AS seeded_rows
FROM MlTaskCostHistory h
JOIN StudentGroup sg ON sg.group_id = h.group_id
GROUP BY sg.group_name
ORDER BY sg.group_name;

SELECT *
FROM MlTaskCostTrainingView
ORDER BY task_completed_at, group_id
LIMIT 15;
