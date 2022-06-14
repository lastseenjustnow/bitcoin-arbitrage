This demo project solves an arbitrage problem. The problem is modelled as a graph with currencies as nodes and
conversion rates as weights of edges. The problem is reduced to a problem of finding all shortest paths for each node of
the graph. Solution is inspired by Floyd-Warshall algorithm, a dynamic programming approach with three state variables,
which are iteration number (number of a currency in a chain), starting node (sell currency side of the trade)
and ending node (buy currency side of the trade).

Other algorithms for the problem of all shortest paths in the graph may be found at:
https://jeffe.cs.illinois.edu/teaching/algorithms/book/09-apsp.pdf

There are four tests written for this solution. Each one tests particular component of the algorithm. Each test can be
run separately or the whole solution may be assembled and run from terminal:
> sbt assembly
> java -jar target/scala-2.12/bitcoin-arbitrage-assembly-1.0.jar

1. Algorithmic complexity analysis:
   Time complexity is biquadratic (O(n^4)) as the algorithm consists of four loops:
    1. First loop is a depth of the paths explored - that is, the length of currency chains. As I would like to explore
       opportunities of all lengths, I have to traverse from chains of length 2 to length of the graph.
    2. Second loop - each node of the graph. I need to calculate all the paths for all the nodes, thus on each iteration
       from the previous step I take each node of the graph.
    3. Third loop - I connect previous node to each other node of the graph in order to find most beneficial paths.
    4. Fourth loop - in it's order, previously chosen nodes have their own destinations to each node, one of which
       should be a starting node of the path.

   Thus, four loops define biquadratic complexity of the algorithm.

2. CHSB is a cryptocurrency, a multi-utility token, meaning its holders get exclusive benefits in the SwissBorg
   ecosystem Key features:
    1. The CHSB Yield Program - modification of concept of deposits. Allows holders of the coke to earn a yield on
       tokens. Tokens earn a passive yield for investors every day they are in the wallet.
    2. Community index - score used to measure the health of the SwissBorg ecosystem.
    3. Protect & Burn mechanism - economic mechanism which allows SwissBorg to impact market value of the token and
       protect it from bearish trends.
    4. Governance rights - enables investors to define the future of the SwissBorg ecosystem.
