# KahootCodingChallenge
Project with my sollution to the kahoot coding challenge. 

I implemented tree like structure to find hints for given query in efficient way.
The idea is to divide each word for sections (nodes).
Each node contains the longest common prefix of words that the node-subtree contains.
The nodes store at least one letter.
The nodes are stored in map where key is the first letter of their stored prefix.

With this structure we can easily find the node which prefix contains the query,
and just return all words from its subtree (as we know that they all start with query).

EXAMPLE:
The tree structure with words: "cat", "car", "carpet", "java", "javascript", "internet", 
would look like this:

The 'c' - key subtree:
.
└── "ca"
      ├── "t"
      └── "r"
           ├── ""
           └── "pet"

The 'j' - key subtree:
.
└── "java"
      ├── ""
      └── "script"
      
The 'i' - key subtree:
.
└── "internet"

Hint-Search complexity is O(k), where k - number of words starting with query.
The presented approach is upgraded version of my previous, similar solution where ech node stored just one letter.
This method reduces the amount of necessary nodes, (in worst case it has the same amount). 
