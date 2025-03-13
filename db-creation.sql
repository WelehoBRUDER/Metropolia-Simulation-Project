DROP DATABASE IF EXISTS amusement_park;
CREATE DATABASE amusement_park;
USE amusement_park;

CREATE TABLE simulation (
	sim_id INT NOT NULL AUTO_INCREMENT,
	time_stamp BIGINT,
	PRIMARY KEY (sim_id)
);

CREATE TABLE service_point (
	sim_id INT NOT NULL,
	simulation_time DECIMAL(12, 2),
	ready_customers INT,
	ticket_customers INT,
	wristband_customers INT,
	unready_customers INT,
	ticket_booth_average DECIMAL(12, 2),
	total_ticket_count INT,
	wristband_average_time DECIMAL(12, 2),
	ticket_average_time DECIMAL(12,2),
	whole_average_time DECIMAL(12, 2),
	ticket_wristband_time_ratio DECIMAL(12, 2),
	#PRIMARY KEY (sim_id),
	FOREIGN KEY (sim_id) REFERENCES simulation(sim_id) ON DELETE CASCADE
);

CREATE TABLE ride (
	sim_id INT NOT NULL,
	ride_id INT NOT NULL,
	count INT,
	average_service_time DECIMAL(12, 2),
	average_queue_time DECIMAL(12, 2),
	variance DECIMAL(12, 2),
	mean DECIMAL(12,2),
	#PRIMARY KEY (ride_id),
	FOREIGN KEY (sim_id) REFERENCES simulation(sim_id) ON DELETE CASCADE
);

CREATE TABLE ticket_booth (
	sim_id INT NOT NULL,
	ticket_booth_id INT,
	count INT,
	average_service_time DECIMAL(12, 2),
	average_queue_time DECIMAL(12, 2),
	#PRIMARY KEY (sim_id),
	FOREIGN KEY (sim_id) REFERENCES simulation(sim_id) ON DELETE CASCADE
);

CREATE TABLE restaurant (
	sim_id INT NOT NULL,
	count INT,
	average_service_time DECIMAL(12, 2),
	average_queue_time DECIMAL(12, 2),
	capacity INT,
	#PRIMARY KEY (sim_id),
	FOREIGN KEY (sim_id) REFERENCES simulation(sim_id) ON DELETE CASCADE
);


DROP USER IF EXISTS 'amusement'@'localhost';
CREATE USER 'amusement'@'localhost' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE ON amusement_park.* TO 'amusement'@'localhost';