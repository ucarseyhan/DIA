Online Client Assignment Problem in Distributed Interactive Application

Quality of user experience in Distributed Interactive Applications
(DIAs) highly depends on the network latencies during the system
execution. In DIAs, each user is assigned to a server and
communication with any other client is performed through its
assigned server. Hence, latency measured between two clients, called
interaction time, consists of two components. One is the latency
between the client and its assigned server, and the other is the
inter-server latency, that is the latency between servers that the
clients are assigned. In this paper, we investigate a real-time
client to server assignment scheme in a DIA where the objective is
to minimize the interaction time among clients. The client
assignment problem is known to be NP-complete and heuristics play an
important role in finding near optimal solutions. We propose two
distributed heuristic algorithms to the online client assignment
problem in a dynamic DIA system. We utilized real-time Internet
latency data on the PlanetLab platform and performed extensive
experiments using geographically distributed PlanetLab nodes where
nodes can arbitrarily join/leave the system. The experimental
results demonstrate that our proposed algorithms can reduce the
maximum interaction time among clients up to %45 compared to an
existing baseline technique.
