package com.pennapps.smartschedule;

/*
 * TODO: Fixed vs. Dynamic Events (Fixed cannot change times, dynamic can)
 * TODO: "Free Time" Calendar and times.
 * 
 * Dealing with load-balancing:
 * 	- Load balancing should avoid the days with the absolute most free time - people probably want that free time
 *    unless they so specify in the settings.
 *  - The optimal days for load balancing are "work days" (ergo, those with average or below average extra tasks).
 * Dealing with splitting:
 *  - Splitting is also dependent on load balancing.
 *  - With load balancing:
 *    - Splitting with load balancing will evenly split a project across a _contiguous_ area, so that it has a definite
 *      start and end date with little lapses (a little every day, in other words).
 *    - Splitting without load balancing will just cram the project into properly-sized cracks.
 *    
 * Any long term-implementation of this project should really use a backtracking algorithm to "guess" what the best solution
 * is via a simple heuristic which rewards contiguity and order; it's extremely annoying to attempt to write programmatively/verbatim.
 */