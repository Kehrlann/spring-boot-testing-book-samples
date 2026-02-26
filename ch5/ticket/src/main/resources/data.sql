INSERT INTO agent (id, name, email) VALUES (1, 'Alice Martin', 'alice@example.com');
INSERT INTO agent (id, name, email) VALUES (2, 'Bob Chen', 'bob@example.com');
INSERT INTO agent (id, name, email) VALUES (3, 'Carol Davis', 'carol@example.com');
INSERT INTO agent (id, name, email) VALUES (4, 'David Kim', 'david@example.com');
INSERT INTO agent (id, name, email) VALUES (5, 'Eva Müller', 'eva@example.com');
INSERT INTO agent (id, name, email) VALUES (6, 'Frank Nguyen', 'frank@example.com');
INSERT INTO agent (id, name, email) VALUES (7, 'Grace Patel', 'grace@example.com');
INSERT INTO agent (id, name, email) VALUES (8, 'Henry Johansson', 'henry@example.com');
INSERT INTO agent (id, name, email) VALUES (9, 'Iris Tanaka', 'iris@example.com');
INSERT INTO agent (id, name, email) VALUES (10, 'Jorge Silva', 'jorge@example.com');
ALTER TABLE agent ALTER COLUMN id RESTART WITH 11;

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

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (6, 'Password reset email not sent', 'Clicking "Forgot password" does not trigger any email.', 'OPEN', 'CRITICAL', NULL, '2026-02-23 07:15:00', '2026-02-23 07:15:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (7, 'Search returns no results', 'The search bar always returns an empty list regardless of query.', 'IN_PROGRESS', 'HIGH', 4, '2026-02-21 10:00:00', '2026-02-22 08:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (8, 'Profile image upload fails', 'Uploading a JPEG profile picture returns a 413 error.', 'OPEN', 'MEDIUM', NULL, '2026-02-22 11:20:00', '2026-02-22 11:20:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (9, 'Notification bell stuck on 99+', 'The notification counter does not reset after viewing.', 'IN_PROGRESS', 'LOW', 5, '2026-02-19 15:45:00', '2026-02-20 09:10:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (10, 'Two-factor auth bypass', 'Certain API endpoints skip 2FA verification.', 'OPEN', 'CRITICAL', NULL, '2026-02-24 06:00:00', '2026-02-24 06:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (11, 'Mobile layout broken on iOS Safari', 'The sidebar overlaps main content on iPhone 15.', 'IN_PROGRESS', 'HIGH', 6, '2026-02-17 13:00:00', '2026-02-18 10:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (12, 'Slow database queries on reports page', 'Monthly report generation times out after 30 seconds.', 'IN_PROGRESS', 'HIGH', 1, '2026-02-16 09:30:00', '2026-02-17 14:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (13, 'Footer links point to 404', 'The "Terms" and "Privacy" footer links lead to missing pages.', 'RESOLVED', 'LOW', 7, '2026-02-14 08:00:00', '2026-02-14 16:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (14, 'Cannot delete account', 'The "Delete my account" button throws a server error.', 'OPEN', 'HIGH', NULL, '2026-02-23 14:00:00', '2026-02-23 14:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (15, 'Email notifications arrive twice', 'Users receive duplicate emails for every event.', 'IN_PROGRESS', 'MEDIUM', 8, '2026-02-20 10:30:00', '2026-02-21 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (16, 'Date picker shows wrong timezone', 'The calendar widget displays UTC instead of the user local time.', 'OPEN', 'MEDIUM', NULL, '2026-02-22 16:00:00', '2026-02-22 16:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (17, 'API rate limiter too aggressive', 'Legitimate users hit 429 errors after only 5 requests per minute.', 'IN_PROGRESS', 'HIGH', 9, '2026-02-19 11:00:00', '2026-02-20 08:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (18, 'Accessibility: missing alt text on images', 'Screen readers cannot describe product images.', 'OPEN', 'MEDIUM', NULL, '2026-02-21 14:15:00', '2026-02-21 14:15:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (19, 'Session expires too quickly', 'Users are logged out after only 5 minutes of inactivity.', 'RESOLVED', 'HIGH', 10, '2026-02-13 09:00:00', '2026-02-14 11:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (20, 'Incorrect currency formatting', 'Prices display as 1000.5 instead of 1,000.50 in the store.', 'OPEN', 'LOW', NULL, '2026-02-23 08:30:00', '2026-02-23 08:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (21, 'Webhook delivery failures', 'Outgoing webhooks silently fail for URLs with self-signed certs.', 'IN_PROGRESS', 'HIGH', 4, '2026-02-18 07:45:00', '2026-02-19 13:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (22, 'CORS error on staging environment', 'Frontend cannot call the API from the staging subdomain.', 'RESOLVED', 'MEDIUM', 5, '2026-02-12 10:00:00', '2026-02-12 17:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (23, 'Pagination off by one', 'Page 2 repeats the last item from page 1.', 'OPEN', 'MEDIUM', NULL, '2026-02-24 09:00:00', '2026-02-24 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (24, 'File upload size limit not enforced', 'Users can upload files larger than the 10 MB limit without error.', 'IN_PROGRESS', 'HIGH', 6, '2026-02-20 12:00:00', '2026-02-21 10:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (25, 'Broken link in onboarding wizard', 'Step 3 of onboarding links to a deprecated docs page.', 'RESOLVED', 'LOW', 7, '2026-02-11 15:00:00', '2026-02-11 17:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (26, 'Memory leak in background worker', 'The report-generation worker grows to 2 GB RAM after a few hours.', 'OPEN', 'CRITICAL', NULL, '2026-02-24 11:30:00', '2026-02-24 11:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (27, 'SSO login redirect loop', 'SAML-based SSO users get stuck in a redirect loop.', 'IN_PROGRESS', 'CRITICAL', 8, '2026-02-22 07:00:00', '2026-02-23 08:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (28, 'Chart tooltips clipped on small screens', 'Analytics chart tooltips are cut off on viewports below 768 px.', 'OPEN', 'LOW', NULL, '2026-02-23 10:00:00', '2026-02-23 10:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (29, 'Audit log missing delete events', 'Deleting a resource does not produce an audit trail entry.', 'IN_PROGRESS', 'HIGH', 9, '2026-02-19 16:30:00', '2026-02-20 14:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (30, 'Logo appears pixelated on Retina', 'The header logo is a low-res PNG; needs an SVG replacement.', 'RESOLVED', 'LOW', 10, '2026-02-09 11:00:00', '2026-02-10 09:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (31, 'Form validation messages not translated', 'Validation errors always show in English regardless of locale.', 'OPEN', 'MEDIUM', NULL, '2026-02-24 13:00:00', '2026-02-24 13:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (32, 'OAuth token refresh race condition', 'Concurrent requests occasionally cause a double-refresh and 401.', 'IN_PROGRESS', 'CRITICAL', 1, '2026-02-21 06:30:00', '2026-02-22 07:45:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (33, 'Drag and drop broken in Firefox', 'Reordering items via drag-and-drop does nothing on Firefox 125.', 'OPEN', 'MEDIUM', NULL, '2026-02-23 15:30:00', '2026-02-23 15:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (34, 'PDF invoice has wrong VAT rate', 'Generated invoices use 19% VAT instead of the configured 21%.', 'IN_PROGRESS', 'HIGH', 2, '2026-02-20 07:00:00', '2026-02-21 08:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (35, 'Keyboard shortcuts conflict with browser', 'Ctrl+S triggers both save and the browser save-page dialog.', 'RESOLVED', 'LOW', 3, '2026-02-08 14:00:00', '2026-02-09 10:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (36, 'Image carousel auto-advances too fast', 'The hero carousel moves every 2 seconds, hard to read text.', 'OPEN', 'LOW', NULL, '2026-02-24 08:00:00', '2026-02-24 08:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (37, 'Cannot sort table by date column', 'Clicking the "Created" column header does not reorder rows.', 'IN_PROGRESS', 'MEDIUM', 4, '2026-02-21 09:00:00', '2026-02-22 11:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (38, 'Stale cache after deployment', 'Users still see the old UI until they hard-refresh.', 'IN_PROGRESS', 'HIGH', 5, '2026-02-22 13:00:00', '2026-02-23 07:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (39, 'Signup form allows disposable emails', 'Users can register with mailinator addresses.', 'OPEN', 'MEDIUM', NULL, '2026-02-23 17:00:00', '2026-02-23 17:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (40, 'GraphQL introspection enabled in prod', 'The production API exposes the full GraphQL schema.', 'OPEN', 'CRITICAL', NULL, '2026-02-25 06:00:00', '2026-02-25 06:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (41, 'User avatar not updating after change', 'Uploading a new avatar shows the old one until page reload.', 'IN_PROGRESS', 'LOW', 6, '2026-02-18 16:00:00', '2026-02-19 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (42, 'Batch import silently skips invalid rows', 'CSV import reports success but drops rows with missing fields.', 'OPEN', 'HIGH', NULL, '2026-02-24 15:00:00', '2026-02-24 15:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (43, 'Email template renders HTML tags', 'The plain-text fallback shows raw <b> and <a> tags.', 'RESOLVED', 'MEDIUM', 7, '2026-02-13 12:00:00', '2026-02-14 08:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (44, 'API docs out of date', 'Swagger UI still references v1 endpoints that were removed.', 'IN_PROGRESS', 'MEDIUM', 8, '2026-02-20 14:00:00', '2026-02-21 11:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (45, 'Cron job runs twice on multi-node deploy', 'Scheduled tasks execute on every node instead of a single leader.', 'OPEN', 'HIGH', NULL, '2026-02-25 07:30:00', '2026-02-25 07:30:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (46, 'XSS in user bio field', 'Entering <script> in the bio executes JavaScript on the profile page.', 'OPEN', 'CRITICAL', NULL, '2026-02-25 08:00:00', '2026-02-25 08:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (47, 'Stripe webhook signature not verified', 'Payment webhooks are accepted without validating the Stripe signature.', 'IN_PROGRESS', 'CRITICAL', 9, '2026-02-23 06:00:00', '2026-02-24 07:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (48, 'Loading spinner never disappears', 'On slow connections the spinner stays even after data loads.', 'RESOLVED', 'LOW', 10, '2026-02-07 10:00:00', '2026-02-08 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (49, 'Role dropdown missing Admin option', 'The user-edit form does not include the ADMIN role in the list.', 'OPEN', 'MEDIUM', NULL, '2026-02-25 09:00:00', '2026-02-25 09:00:00');

INSERT INTO ticket (id, title, description, status, priority, assigned_agent_id, created_at, updated_at)
VALUES (50, 'Database connection pool exhaustion', 'Under load the app runs out of connections and returns 503.', 'IN_PROGRESS', 'CRITICAL', 1, '2026-02-24 05:00:00', '2026-02-25 06:30:00');

ALTER TABLE ticket ALTER COLUMN id RESTART WITH 51;
