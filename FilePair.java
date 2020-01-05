public class FilePair {
  
  String fileType;
  int popularity;
  String key;
  FilePair(String fileType, int popularity, String key) {
    this.fileType = fileType;
    this.popularity = popularity;
    this.key = key;
  }
  
  String getFileType() {
    return fileType;
  }
  
  int popularity() {
    return popularity;
  }
  
  String getKey() {
    return key;
  }
}