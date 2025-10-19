package com.genapp.exception;

/**
 * PolicyNotFoundException - Thrown when a policy is not found in the database
 *
 * Replaces the COBOL NOTFND condition from CICS:
 *   EXEC CICS READ FILE('KSDSPOLY')
 *       INTO WS-POLICY-REC
 *       NOTFND
 *           SET POLICY-NOT-FOUND-FLAG TO TRUE
 *       END-EXEC.
 *
 * Old COBOL pattern: Check NOTFND flag and set error code
 * New Spring pattern: Throw exception, caught by GlobalExceptionHandler
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
public class PolicyNotFoundException extends RuntimeException {

    public PolicyNotFoundException(String message) {
        super(message);
    }

    public PolicyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
