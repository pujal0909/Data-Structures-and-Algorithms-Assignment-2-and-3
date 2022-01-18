/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt   (compress)
 *  Execution:    java LZWmod + < input.txt   (expand)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/
//compressed ratio = original/compressed size
public class LZWmod {
    private static final int R = 256;  // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static int maxW = 26;
    private static boolean reset;

    public static void compress() {
      //TODO: Modify TSTmod so that the key is a
      //StringBuilder instead of String
        if(reset == false)
        {
            BinaryStdOut.write(false);
        }
        else
        {
            BinaryStdOut.write(true);
        }
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        //initialize the current string
        StringBuilder current = new StringBuilder();
        //read and append the first char
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) { 
            codeword = st.get(current);
            //TODO: read and append the next char to current
            c = BinaryStdIn.readChar();
            current.append(c);

            if(!st.contains(current)){
              BinaryStdOut.write(codeword, W); //write the codeword to the table with codeword width
              //if the code is less than the number of codewords in the codebook
              //the 3 cases:
              if (code < L)    // Add to symbol table if not full
              {
                  //put the codeword into the codebook
                  st.put(current, code++); 
              }
              else if(code >= L) //if code is greater than or equal to the max number of codewords in the codebook
              {
                  if(W < 16) //and if the codeword width is less than 16
                  {
                      W+=1; //increment the codeword width by 1
                      L = (int) Math.pow(2,W); //the number of codewords is 2^W
                      st.put(current, code++); //put the word into the codebook

                  }
              }
              else
              {
                  //check for reset
                  if(reset)
                  {
                     st = new TSTmod<Integer>();
                     //as long as R is less that 256
                     for(int i = 0; i< R; i++)
                     {
                        //the input char is put into the symbol table
                        st.put(new StringBuilder("" + (char) i), i); 
                     }
                     code = R + 1;
                     //after the loop finishes when R equals 257, set the codeword width to 9 and the max number of codes is 512 by default
                     W = 9;
                     L = (int) Math.pow(2,W);
                
                  }
              }

              //reset current to have nothing
              current = new StringBuilder();
              //append the last letter added to the stringbuilder
              current.append(c);
            
            }
        }
        
        //current.delete(0, current.length()-1);
        
        BinaryStdOut.write(st.get(current),W);
        //TODO: Write the codeword of whatever remains
        //in current

        BinaryStdOut.write(R, W); //Write EOF
        BinaryStdOut.close(); //close the file
    }
    //higher compression ratio is better
    //orginal did not use adaptive codeword width 
    //modified does use it, so it makes a bigger symbol table
    //both lego and frosty have the worst ratio (smallest ratio) 
    //because they are more detailed since they are images 
    //lego and frosty --> original ratio = compression ratio for unix

    public static void expand() {
        int maxL = (int) Math.pow(2,16); //maximum number of codewords in codebook
        String[] st = new String[maxL]; //maximum codeword size in symbol table
        int i; // next available codeword value
        int code = R+1;
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

    
        reset = BinaryStdIn.readBoolean();
        int codeword = BinaryStdIn.readInt(W); //the first codeword is read in
        String val = st[codeword]; //codeword is in the symbol table
        

        while (true) {

            //the 3 cases:
            if(i >= L) //if the codeword is less than the maximum number of codewords
            {
                if(W < 16) //and if the codeword width is less than 16
                {

                    W++; //increment the width
                    L = (int) Math.pow(2,W); //the number of codewords is 2^W

                }
            }
            else
            {
                //check for reset 
                if(reset)
                {
                    if(W == 16 && i == L) //if the code is less than codebook size and the codeword width is 16
                    {
                        
                        st = new String[maxL];
                        for(i = 0; i < R; i++) //as long as R is less than 256
                        {
                            st[i] = "" + (char) i;
                        }
                        st[i++] = ""; 
                        //when loop ends when R is 257, width is set to 9 and max number of codes is 512 by default 
                        W = 9;
                        L = 512;
                        i = R +1;
                    }
                }
                
            }

            BinaryStdOut.write(val); //the string is written into the new file
            codeword = BinaryStdIn.readInt(W); 
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            //System.err.println(i + " " + s);

            if (i < L) st[i++] = val + s.charAt(0); //if the symbol table is not full

        
            val = s;
        

        }
        BinaryStdOut.close(); //close the file
    }



    public static void main(String[] args) {
        if      (args[0].equals("-"))
        {
            if(args[1].equals("r")) //if the user wants to reset the codebook
            {
                reset = true; 
            }
            else
            {
                reset = false;
            }
            compress();
        }
        else if (args[0].equals("+")) 
        {
            expand();
        } 
        else throw new RuntimeException("Illegal command line argument");
    }

}
