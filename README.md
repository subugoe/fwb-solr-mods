# Solr modifications for FWB (Frühneuhochdeutsches Wörterbuch)

## Description

The classes contained in this project extend standard Solr classes. There are for example custom Filter Factories,
Filters, and Search Handlers that change the functionality of those classes or add something new to the standard functionality.

## Compilation

You need Java JDK 7 or higher and Maven 3 or higher.
To compile the project, go into its main directory and execute 

``` mvn clean package ```

The Java binary archive .jar file will be placed into the target/ directory.

## Usage

To use the classes, you must put the compiled .jar file to a place where Solr can find it and then reference the 
file in the solrconfig.xml. For example, if you put the .jar into the conf directory of your Solr's core,
you must have the following entry in the solrconfig.xml:

``` <lib path="conf/fwb-solr-mods.jar" /> ```

(The root directory is the main directory of the core, not the directory of the solrconfig.xml file).

After reloading the core or restarting Solr, you can use the custom class names in the solrconfig.xml 
and in the schema.xml files by just replacing Solr's standard class names in the class="..." attributes. 
For example:

``` <requestHandler name="/search" class="sub.fwb.QueryModifyingSearchHandler">  ```

in solrconfig.xml, or:

``` <filter class="sub.fwb.UmlautFilterFactory" file="umlaut_mappings.txt" /> ```

in schema.xml.
  
For more details, you can look at the complete files on Github: 
https://github.com/subugoe/fwb-index-creator/tree/master/solr/fwb/conf

## Details

* UmlautFilterFactory and UmlautFilter

  These classes deal with German umlauts and other accented letters. The Factory expects a file (parameter file="...")
  containing the mappings in the form 

  ä:a,ae

  on each line. In this example, you will have the terms bär, bar, and baer
  in your index, if the original text contains the word bär. The three terms will have the same offsets, so that
  the highlighter will highlight the original bär, no matter which of the three terms you search.
  
  The Filter can also deal with so-called combining letters, like for example a little i over u: uͥ. If you want to 
  also find uͥ with a normal u, you can add the following to the mapping file:

  U+0365:

  After the colon, there is nothing there, i. e., an empty string.
  
  Here is an example of a complete file: https://github.com/subugoe/fwb-index-creator/blob/master/solr/fwb/conf/umlaut_mappings_for_quotes.txt.
  
* LemmaNormalizingFilterFactory and LemmaNormalizingFilter

  Normally, all punctuation characters are removed in the index.
  Some lemmas in the FWB project contain non-letter characters that nonetheless must be found when typed in by the user. Examples are 
  parentheses, brackets, or the pipe symbol. This Filter "normalizes" such lemmas by creating alternative spellings
  in the index. The lemma "acht|ek" will get two search terms on the same position: "acht|ek" and "achtek", hence 
  the pipe becomes optional while searching. Lemmas with parentheses and brackets are a little more complicated. The lemma
  "ampt(s)kleid" becomes: "ampt(s)kleid", "amptskleid", "amptkleid", thus the parentheses and the inner letters
  are optional.
  
* WildcardsAcceptingPatternReplaceFilterFactory

  This class does the same as the standard PatternReplaceFilterFactory in Solr. However, the standard one does nothing
  at query time, if the user query contains wildcards (* or ?). The only difference in this class is that it implements
  a special Java interface (MultiTermAwareComponent) that makes it process queries with wildcards, too.
  
* SimplifiedSimilarity

  With the help of this Similarity, Solr ignores some factors while computing relevance scores of found documents. 
  For example, idf (inverse document frequency) and field lengths are always considered to be 1.0.
  
* QueryModifyingSearchHandler

  As the name suggests, this Handler modifies the actual user query by, for example, expanding a simple search word
  into a more complex query containing wildcards. This way, index terms that only have that word as a substring
  can also be found.
  ...
