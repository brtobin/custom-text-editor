@SuppressWarnings("serial")
public class NoFileTypeException extends Exception {
  public NoFileTypeException() {
    
  }
  
  public String getMessage() {
    return "Please enter a file type to save as";
  }
}
