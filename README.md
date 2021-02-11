# Java Bot #

##### Build the project manually #####
* ``` gradlew build ```
* Check if it works: ``` java -jar build/libs/java-bot.jar ```

##### Use in IntelliJ IDEA #####

* Choose File->New->Project From Existing Sources
* Follow the instructions
* On the last screen check:
    * Use auto-import
    * Use gradle 'wrapper' task configuration
    
    
## TODO ## 

 * Improve shooting
   * Aiming: rangecheck (distance < x)
   * Follow-up : go to enemy position to shoot
 
 * Memory
   * Workers scouting: communicate to warriors close positions
 
 * Check
   * Can we avoid FF? Check friendlies in range, check if aiming improvements avoid FF.
 
 * Pathing
   * Workers: path to resources in order. Check if resource is still there.
   
  
