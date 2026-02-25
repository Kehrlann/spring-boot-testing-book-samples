INSERT INTO agent (id, name, email) VALUES (1, 'Alice Martin', 'alice@example.com');
INSERT INTO agent (id, name, email) VALUES (2, 'Bob Chen', 'bob@example.com');
INSERT INTO agent (id, name, email) VALUES (3, 'Carol Davis', 'carol@example.com');
ALTER TABLE agent ALTER COLUMN id RESTART WITH 4;

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (1, 'Login page returns 500', 'Users report a 500 error when submitting the login form.', 'OPEN', 'CRITICAL', NULL, '2026-02-20 09:00:00', '2026-02-20 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (2, 'Dashboard loads slowly', 'The main dashboard takes over 10 seconds to render.', 'IN_PROGRESS', 'HIGH', 1, '2026-02-18 14:30:00', '2026-02-19 10:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (3, 'Typo in welcome email', 'The welcome email says "Welcom" instead of "Welcome".', 'RESOLVED', 'LOW', 2, '2026-02-15 11:00:00', '2026-02-16 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (4, 'Export CSV broken', 'Clicking the CSV export button produces an empty file.', 'OPEN', 'MEDIUM', NULL, '2026-02-22 08:45:00', '2026-02-22 08:45:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (5, 'Add dark mode support', 'Users have requested a dark mode option in settings.', 'IN_PROGRESS', 'LOW', 3, '2026-02-10 16:00:00', '2026-02-12 11:00:00');
ALTER TABLE ticket ALTER COLUMN id RESTART WITH 6;

INSERT INTO comment (id, ticket_id, author_name, content, created_at)
VALUES (1, 2, 'Alice Martin', 'Investigating the N+1 query on the dashboard endpoint.', '2026-02-19 10:00:00');

INSERT INTO comment (id, ticket_id, author_name, content, created_at)
VALUES (2, 2, 'Bob Chen', 'Could be related to the recent migration. Checking.', '2026-02-19 11:30:00');

INSERT INTO comment (id, ticket_id, author_name, content, created_at)
VALUES (3, 3, 'Bob Chen', 'Fixed the typo and redeployed the email template.', '2026-02-16 09:00:00');
ALTER TABLE comment ALTER COLUMN id RESTART WITH 4;
