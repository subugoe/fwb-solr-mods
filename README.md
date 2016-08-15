# Solr modifications for FWB (Frühneuhochdeutsches Wörterbuch)

## Description

The classes contained in this project extend standard Solr classes. There are for example custom Filter Factories,
Filters, and Search Handlers that change their functionality or add something new to the standard functionality.

* UmlautFilterFactory and UmlautFilter

  These classes deal with German umlauts and other accented letters. The Factory expects a file (parameter file="...")
  containing the mappings in the form 

  ä:a,ae

  on each line. In this example, you will have the terms bär, bar, and baer
  in your index, if the original text contains the word bär. The three terms will have the same offsets, so that
  the highlighter will highlight the original bär, no matter which of the three terms you search for.
  
  The Filter can also deal with so-called combining letters, like for example a little i over u: uͥ. If you want to 
  also find uͥ with a normal u, you can add the following to the mapping file:

  U+0365:

  After the colon, there is nothing there, i. e., an empty string.
  
  Here is an example of a complete file: https://github.com/subugoe/fwb-index-creator/blob/master/solr/fwb/conf/umlaut_mappings_for_quotes.txt.
  
* LemmaNormalizingFilterFactory and LemmaNormalizingFilter

  ...

## Compilation

You need Java JDK 7 or higher and Maven 3 or higher.
To compile the project, go into its main directory and execute 

``` mvn clean package ```

The Java binary archive .jar file will be placed into the target/ directory.

## Usage


  
  
