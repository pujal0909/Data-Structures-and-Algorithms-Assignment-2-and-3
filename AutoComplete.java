//TO-DO Add necessary imports
import java.io.*;
import java.util.*;

public class AutoComplete{

  //TO-DO: Add instance variable: you should have at least the tree root
  private DLBNode root;

  public AutoComplete(String dictFile) throws java.io.IOException {
    //TO-DO Initialize the instance variables  
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      //TO-DO call the public add method or the private helper method if you have one
      add(word);
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add words
  //if the letter and letter are not the same, then make it a sibling
  //if the letter and letter are the same, make it a child
  public void add(StringBuilder word){
    //TO-DO Implement this method
      if (word == null) throw new IllegalArgumentException("calls put() with a null key");
      
      root = add(root, word, 0);

    }
      //add helper method
      //adding words to the trie
      private DLBNode add(DLBNode x, StringBuilder word, int pos)
      {
      
          DLBNode result = x;
          //if node is null
          if(result == null)
          {
              //create a new DLBNode at that position
              //initial score of 0
              result = new DLBNode(word.charAt(pos),0);
              //if the position is less than the word's length minus 1
              if(pos < word.length()-1)
              {
                  //recurse on the child node because it is not a word yet
                  //pos + 1 because child is placed at the next spot down a trie
                  result.child = add(result.child, word, pos + 1);
              }
              else
              {
                  //the position equals the word length minus 1 or greater than it
                  //it is a word
                  result.isWord = true;
              }
          }
          //if the letter is equal to the letter at the position
          else if(result.data == word.charAt(pos))
          {
              //if the position is less than the word's length minus 1
              if(pos < word.length()-1)
              {   
                  //recurse on the child node because it is not a word yet
                  result.child = add(result.child, word, pos + 1);
              }
              else
              {
                  //the position equals the word length minus 1 or greater than it
                  //it is a word
                  result.isWord = true;
              }
          }
          // if the letter does not equal the letter at the position
          else 
          {
              //recurse on the sibling node 
              result.sibling = add(result.sibling, word, pos);
          }
          return result;

      }


  //increment the score of word
  public void notifyWordSelected(StringBuilder word){
    //TO-DO Implement this method
    DLBNode num = getNode(root, word.toString(), 0); //get last node by using getNode
    if(num != null) //if the last node is not null, then increment the score
    {
        num.score++;
    }

  }

  //retreive the score of word
  public int getScore(StringBuilder word){
    //TO-DO Implement this method
    DLBNode num = getNode(root, word.toString(), 0); //get last node by using getNode
    if(num == null) //if the last node is null, return zero
    {
      return 0;
    }
    return num.score; //otherwise return the score
  }
 
  /**
   * Part 2: retrieve word suggestions in sorted order.
   */
  
  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
    public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    //TO-DO Implement this method
      ArrayList<Suggestion> sorted = new ArrayList<Suggestion>(); //creating a new arraylist

      //gets last node using getNode
      DLBNode firstNode = getNode(root, word.toString(),0);     
      
      //checking if the last node is null
      //because if it is null, then child and sibling will be null also
      if(firstNode != null)
      { 
          //checking if it is a prefix
          //if the last node found is a prefix, then add it to the list
          if(firstNode.isWord)
          {
              //add a new suggestion
              sorted.add(new Suggestion(word, firstNode.score));
              
          }
          //if it is not null, call collect method on its child
          collect(firstNode.child, sorted, word); 
      }
      //sort the list in descending order based off scores
      Collections.sort(sorted, Collections.reverseOrder());

      return sorted;
  }

  /**
   * Helper methods for debugging.
   */

   //collect finds all the words that have the given stringbuilder as a prefix
   private void collect(DLBNode x, ArrayList<Suggestion> queue, StringBuilder word) 
   {
        
        if (x == null) 
        {
           return;
        }
        DLBNode curr = x;
        while(curr != null)
        {

          word.append(curr.data);
          //if it is a word, then add it to the queue
          if(curr.isWord){
              
              // new stringbuilder collects and retrieves new words, so it is needed otherwise old words will print over and over
              queue.add(new Suggestion(new StringBuilder(word.substring(0, word.length())),curr.score));
              
          }
          //if child node is not null
          if(curr.child != null)
          {
              //call collect on the child node
              collect(curr.child, queue, word);
          }
          word.deleteCharAt(word.length()-1);
          //curr is now the sibling
          curr = curr.sibling;

        }


    }



  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          return node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair. 
  //This class should be Comparable to itself.
  public class Suggestion implements Comparable<Suggestion> /*.....*/ {
    //TO-DO Fill in the fields and methods for this class. Make sure to have them public as they will be accessed from the test program A2Test.java.
    public StringBuilder word;
    public Integer score;

    public Suggestion(StringBuilder word, int score)
    {
        this.word = word;
        this.score = score;
    }

    public int compareTo(Suggestion v)
    { 

        return this.score.compareTo(v.score);
   
    }

  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
