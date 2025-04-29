-- Insert data into Department table
INSERT IGNORE INTO Department (id, name, location) VALUES
(1, 'IT', 'STPI'),
(2, 'Human Resources', 'Academy'),
(3, 'QA', 'SDB1'),
(4, 'Finanance', 'Academy');

-- Insert data into Employee table
INSERT IGNORE INTO Employee (id, first_name, last_name, email, department_id) VALUES
(1, 'Ayesha', 'Doe', 'Ayesha@cts.com', 1),
(2, 'Sakthi', 'Vasan', 'Sakthi@cts.com', 1),
(3, 'Arun', 'Karthik', 'arun@cts.com', 1),
(4, 'Gaythri', 'S', 'gaythri@cts.com', 1),
(5, 'Gaythri', 'E', 'gaythri@cts.com', 1),
(6, 'Maria', 'Coach', 'maria@cts.com', 2),
(7, 'elangovan', 's', 'elangovan@cts.com', 4);