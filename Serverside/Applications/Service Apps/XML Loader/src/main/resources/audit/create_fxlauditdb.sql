CREATE SCHEMA IF NOT EXISTS fxlauditdb AUTHORIZATION fxlauditdbadmin;


/*
 * audit_files table
 */

CREATE TABLE IF NOT EXISTS fxlauditdb.audit_files (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	filename VARCHAR(255) NOT NULL,
	fileuri VARCHAR(255) NOT NULL,
	targetinstance VARCHAR(255),
	workgroup VARCHAR(255) NOT NULL,
	dispatched BOOLEAN NOT NULL
);

CREATE INDEX idx_audit_files ON fxlauditdb.audit_files(id);


/*
 * audit_milestones table
 */

CREATE TABLE IF NOT EXISTS fxlauditdb.audit_milestones (
	fileid VARCHAR(36) NOT NULL PRIMARY KEY,
	status INTEGER NOT NULL,
	milestone INTEGER NOT NULL,
	milestonedate DATE NOT NULL,
	milestonetime TIME NOT NULL
);

CREATE INDEX idx_audit_milestones ON fxlauditdb.audit_milestones(fileid, milestonedate);


/*
 * status_values table
 */

DROP TABLE IF EXISTS fxlauditdb.status_values;

CREATE TABLE IF NOT EXISTS fxlauditdb.status_values (
	id INTEGER NOT NULL PRIMARY KEY,
	status VARCHAR(255) NOT NULL
);

INSERT INTO fxlauditdb.status_values (id, status) VALUES (1, 'INPROGRESS');
INSERT INTO fxlauditdb.status_values (id, status) VALUES (2, 'PROCESSED');
INSERT INTO fxlauditdb.status_values (id, status) VALUES (3, 'ERROR');


/*
 * milestone_values table
 */

DROP TABLE IF EXISTS fxlauditdb.milestone_values;

CREATE TABLE IF NOT EXISTS fxlauditdb.milestone_values (
	id INTEGER NOT NULL PRIMARY KEY,
	milestone VARCHAR(255) NOT NULL
);

INSERT INTO fxlauditdb.milestone_values (id, milestone) VALUES (1, 'PICKUP');
INSERT INTO fxlauditdb.milestone_values (id, milestone) VALUES (2, 'VALIDATION');
INSERT INTO fxlauditdb.milestone_values (id, milestone) VALUES (3, 'LOAD');
INSERT INTO fxlauditdb.milestone_values (id, milestone) VALUES (4, 'DISPATCH');


/*
 * file_errors table
 */

CREATE TABLE IF NOT EXISTS fxlauditdb.file_errors (
	fileid VARCHAR(36) NOT NULL PRIMARY KEY,
	errordetail CLOB NOT NULL
);

CREATE INDEX idx_file_errors ON fxlauditdb.file_errors(fileid);


/*
 * foreign key constraints
 */

ALTER TABLE fxlauditdb.audit_milestones ADD CONSTRAINT FKstatus_audit_milestones FOREIGN KEY (status) REFERENCES fxlauditdb.status_values (id);
ALTER TABLE fxlauditdb.audit_milestones ADD CONSTRAINT FKmilestone_audit_milestones FOREIGN KEY (milestone) REFERENCES fxlauditdb.milestone_values (id);


/*
 * grants
 */

GRANT ALL ON fxlauditdb.audit_files TO fxlauditdbadmin;
GRANT ALL ON fxlauditdb.audit_milestones TO fxlauditdbadmin;
GRANT ALL ON fxlauditdb.status_values TO fxlauditdbadmin;
GRANT ALL ON fxlauditdb.milestone_values TO fxlauditdbadmin;


COMMIT;