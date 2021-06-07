import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


public class DictionaryTry {


    public static void main(String[] args)
    {
        // read text from the file
        readFromTextFileEachWord("input.txt");

    }   // main

    public static void readFromTextFileEachWord(String textFileName)
    {
        try
        {
            boolean currentGroupIsAWord = false;

            String currentGroup = "";

            int currentCharacterAsInt;
            BufferedReader fileReader = new BufferedReader(new FileReader(textFileName));

            while((currentCharacterAsInt = fileReader.read()) != -1)
            {
                char currentChar = (char)currentCharacterAsInt;

                if( (Character.isLetter(currentChar) || currentChar == '\'') != currentGroupIsAWord)
                {
                    if(currentGroupIsAWord)
                        responseFromApi(currentGroup);
                    currentGroup = "";
                    currentGroupIsAWord = !currentGroupIsAWord;
                }
                else if(!Character.isLetter(currentChar) && !Character.isWhitespace(currentChar))
                    responseFromApi(String.valueOf(currentChar));

                currentGroup += currentChar;
            } // while

            if (currentGroupIsAWord && !currentGroup.equals(""))
                responseFromApi(currentGroup);

        }
        catch (FileNotFoundException e)
        {
            System.err.println("File could not be found " + e);
            e.printStackTrace();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

    } // readFromTextFileEachWord

    public static void responseFromApi(String wordToFindSynonym)
    {

        String callToAPI = "https://api.dictionaryapi.dev/api/v2/entries/en_GB/" + wordToFindSynonym;
        BufferedReader in = null;
        try
        {
            // the url link
            URL gutenbergUrl = new URL(callToAPI);

            // How we get the data from the URL
            in = new BufferedReader(new InputStreamReader(gutenbergUrl.openStream()));

            String l = null;
            String responseWithSynonym = "";
            String fullResponse = "";

            while ((l=in.readLine())!=null) {

                fullResponse = l.toString();
                // if there is a synonym to the word, set the response to the array of synonyms
                if (fullResponse.contains("synonyms"))
                    responseWithSynonym = l.substring(l.indexOf("\"synonyms\""), l.indexOf(']', l.indexOf("\"synonyms\""))+ 1);
                else
                    writeTheSynonymVersionToTextFile(wordToFindSynonym);

            }
            // if there is a synonym, write the string to a text file
            if(responseWithSynonym != "")
            {
                // System.out.println(responseWithSynonym);
                writeTheSynonymVersionToTextFile(responseWithSynonym);

            }
            in.close();


        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {

            writeTheSynonymVersionToTextFile(wordToFindSynonym);
        }
        // finally try to close the input, if there is a problem with closing the input report the exception
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch(IOException exception)
            {
                System.err.println("Could not close input" + exception);
            }
        }


    }

    public static void writeTheSynonymVersionToTextFile(String textToPutIntoTheTextFile)
    {
        // System.out.println(textToPutIntoTheTextFile);
        String wordToAddToTextFile = textToPutIntoTheTextFile;
        System.out.println(wordToAddToTextFile);
        PrintWriter output = null;
        try {
             output = new PrintWriter(new FileWriter("output.txt", true));

            // if what is passed to it is the array of synonyms, parse the array to get the first synonym
            if (textToPutIntoTheTextFile.contains("\"synonyms\":")) {
                textToPutIntoTheTextFile = textToPutIntoTheTextFile.replace("\"synonyms\":", "");
                textToPutIntoTheTextFile = textToPutIntoTheTextFile.replace("[", "");
                textToPutIntoTheTextFile = textToPutIntoTheTextFile.replace("]", "");
                textToPutIntoTheTextFile = textToPutIntoTheTextFile.replace("\"", "");

                String[] arrayOfResponses = textToPutIntoTheTextFile.split(",");
//                System.out.println(arrayOfResponses[0]);
                wordToAddToTextFile = arrayOfResponses[0];
            }

            // if it is a word, there should be a space before it, punctuation does not require a space before it
            if(Character.isLetter(wordToAddToTextFile.charAt(0)))
            {
                wordToAddToTextFile = " " + wordToAddToTextFile;
            }

            output.write(wordToAddToTextFile);
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        finally
        {
          if (output!=null)
          {
              output.close();
              if(output.checkError())
                  System.err.println("Something went wrong with the output");
          }
        }

    }
} // class CharacterCount
