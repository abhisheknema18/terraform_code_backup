CREATE SCHEMA IF NOT EXISTS feaauditdb AUTHORIZATION feaauditdbadmin;

/*
 * audit_files table
 */

CREATE TABLE IF NOT EXISTS feaauditdb.extract_audit (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	returnid INTEGER NOT NULL,
	instance VARCHAR(255),
	extracted BOOLEAN NOT NULL,
	auditdate DATE NOT NULL,
	audittime TIME NOT NULL
);

CREATE INDEX idx_extract_audit ON feaauditdb.extract_audit(id, auditdate);


/*
 * audit_files table
 */

CREATE TABLE IF NOT EXISTS feaauditdb.redelivery (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	attempts INTEGER NOT NULL,
	message CLOB
);

CREATE INDEX idx_redelivery ON feaauditdb.redelivery(id, attempts);


/*
 * audit_files table
 */

CREATE TABLE IF NOT EXISTS feaauditdb.extract_errors (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	errordetail CLOB NOT NULL
);

CREATE INDEX idx_extract_errors ON feaauditdb.extract_errors(id);

/*
 * grants
 */

GRANT ALL ON feaauditdb.extract_audit TO feaauditdbadmin;
GRANT ALL ON feaauditdb.redelivery TO feaauditdbadmin;
GRANT ALL ON feaauditdb.extract_errors TO feaauditdbadmin;
 
COMMIT;