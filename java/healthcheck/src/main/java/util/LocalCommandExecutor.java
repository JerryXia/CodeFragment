/**
 * 
 */
/**
 * @author guqk
 *
 */
package util;

public interface LocalCommandExecutor {
    ExecuteResult executeCommand(String command, long timeout);
}