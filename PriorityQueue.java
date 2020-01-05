import java.util.ArrayList;

public class PriorityQueue {
  public ArrayList<FilePair> fileTypes;
  FilePair root;
  int time = 1;

  /*
  public PriorityQueue(ArrayList<FilePair> types) {
    this.fileTypes = types;
    fileTypes.add(0, null);
  } */
  
  
  public PriorityQueue() {
    fileTypes = new ArrayList<FilePair>();
    fileTypes.add(0, null);
  } 
  
  public void beforeAnything() {
    for (FilePair d: fileTypes) {
      if (d != null)
      System.out.println(d.getFileType());
    }
  }

  public void add(FilePair fp) {
    if (fileTypes.size() == 1) {
      fileTypes.add(fp); // if heap is empty, simply add the new FilePair
      root = fileTypes.get(1);
    } else {
      fileTypes.add(fp);
      this.addHelper(fileTypes.get(fileTypes.size() - 1));
      root = fileTypes.get(1);
    }
  }

  public FilePair removeMax() {
    FilePair removed;
    FilePair leaf;
    
    if (fileTypes.size() == 2) {
      removed = fileTypes.get(1);
      fileTypes.remove(1);
      root = null;
    } else {
      leaf = fileTypes.get(fileTypes.size()-1);
      removed = fileTypes.remove(1);
      fileTypes.add(1, leaf);
      fileTypes.remove(leaf);
      root = fileTypes.get(1);
      removeHelper(root); // propogate down
      root = fileTypes.get(1);
    }
    return removed;
  }

  private void removeHelper(FilePair current) {
    int leftIdx = fileTypes.indexOf(current) * 2;
    int rightIdx = fileTypes.indexOf(current) * 2 + 1;
    int currentIdx = fileTypes.indexOf(current);
    int leftChild = -1;
    int rightChild = -1;
    FilePair temp;
    if (!(leftIdx > fileTypes.size()-1 || leftIdx < 1)) {
      leftChild = fileTypes.get(leftIdx).popularity();
    }
    if (!(rightIdx > fileTypes.size()-1 || rightIdx < 1)) {
      rightChild = fileTypes.get(rightIdx).popularity();
    }
    // if both children exist
    if (leftChild != -1 && rightChild != -1) {
      if (current.popularity() < leftChild
          || current.popularity() < rightChild) {
        // swap places w/ the larger child
        if (leftChild < rightChild) {
          temp = current;
          fileTypes.set(currentIdx, fileTypes.get(rightIdx));
          fileTypes.set(rightIdx, temp);
          current = fileTypes.get(rightIdx);
          removeHelper(current);
        } else {
          temp = current;
          fileTypes.set(currentIdx, fileTypes.get(leftIdx));
          fileTypes.set(leftIdx, temp);
          current = fileTypes.get(leftIdx);
          removeHelper(current);
        }
      } else {
        return;
      }
    } else if (leftChild != -1 && rightChild == -1) { // else, if just the left child exists
      if (current.popularity() < leftChild) { // ...and the left child is larger
        // switch places with the left child
        temp = current;
        fileTypes.set(currentIdx, fileTypes.get(leftIdx));
        fileTypes.set(leftIdx, temp);
        current = fileTypes.get(leftIdx);
        removeHelper(current);
      } else {
        return;
      }
    } else {
      return;
    }
  }

  private void addHelper(FilePair current) {
    int currentIdx = fileTypes.indexOf(current);
    int parentIdx = -1;
    FilePair temp;
    if (currentIdx == 1) {
      return;
    } else {
      parentIdx = (int) Math.floor(currentIdx / 2);
    }
    if (parentIdx != -1) {
      if (fileTypes.get(currentIdx).popularity() > fileTypes.get(parentIdx)
          .popularity()) {
        temp = fileTypes.get(currentIdx);
        fileTypes.set(currentIdx, fileTypes.get(parentIdx));
        fileTypes.set(parentIdx, temp);
        currentIdx = parentIdx;
        addHelper(fileTypes.get(currentIdx));
      } else {
        return;
      }
    }
  }

  public void printList() {
    for (FilePair type : fileTypes) {
      if (type == null) {
        System.out.print("null ");
      } else {
        System.out.print(type.popularity() + " ");
      }
    }
  }

  public static void main(String args[]) {
   
}
}
