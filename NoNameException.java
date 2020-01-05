@SuppressWarnings("serial")
public class NoNameException extends Exception {
  String message;

  public NoNameException()  {
  }

  public String getMessage() {
    return "Please enter a name for the file";
  }
}
