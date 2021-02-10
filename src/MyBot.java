import java.util.ArrayList;
import java.util.List;
import lia.Api;
import lia.Bot;
import lia.Constants;
import lia.MathUtil;
import lia.NetworkingClient;
import lia.api.GameState;
import lia.api.OpponentInView;
import lia.api.ResourceInView;
import lia.api.Rotation;
import lia.api.Speed;
import lia.api.UnitData;
import lia.api.UnitType;

/**
 * Initial implementation keeps picking random locations on the map and sending units there. Worker units collect
 * resources if they see them while warrior units shoot if they see opponents.
 */
public class MyBot implements Bot {

  CustomMemory memory = new CustomMemory();

  // This method is called 10 times per game second and holds current
  // game state. Use Api object to call actions on your units.
  // - GameState reference: https://docs.liagame.com/api/#gamestate
  // - Api reference:       https://docs.liagame.com/api/#api-object
  @Override
  public void update(GameState state, Api api) {

    /* INIT ROUTINE (WHEN GAME TIME = 0)*/
    if (state.time == 0) {

      return;
    }
    /* END OF INIT ROUTINE */
    /* SPAWN ROUTINE */
    // If you have enough resources to spawn a new warrior unit then spawn it.
    int numberOfWorkers = 0;
    for (UnitData unit : state.units) {
      if (unit.type == UnitType.WORKER) {
        numberOfWorkers++;
      }
    }

// If from all of your units less than 60% are workers
// and you have enough resources, then create a new worker.
    if (numberOfWorkers / (float) state.units.length < 0.6f && state.time < Constants.STOP_SPAWNING_AFTER / 2) {
      if (state.resources >= Constants.WORKER_PRICE) {
        api.spawnUnit(UnitType.WORKER);
      }
    }
// Else if you can, spawn a new warrior
    else if (state.resources >= Constants.WARRIOR_PRICE) {
      api.spawnUnit(UnitType.WARRIOR);
    }

    /* END OF SPAWN ROUTINE */

    /*START OF UNITS LOOP */
    // We iterate through all of our units that are still alive.
    for (int i = 0; i < state.units.length; i++) {
      UnitData unit = state.units[i];
      // If the unit is not going anywhere, we send it
      // to a random valid location on the map.

      /* START OF MOVEMENT ROUTINE */
      if (unit.navigationPath.length == 0) {

        // Generate new x and y until you get a position on the map
        // where there is no obstacle. Then move the unit there.
        while (true) {
          int x = (int) (Math.random() * Constants.MAP_WIDTH);
          int y = (int) (Math.random() * Constants.MAP_HEIGHT);

          // Map is a 2D array of booleans. If map[x][y] equals false it means that
          // at (x,y) there is no obstacle and we can safely move our unit there.
          if (!Constants.MAP[x][y]) {
            api.navigationStart(unit.id, x, y);
            break;
          }
        }
      }

      /*END OF UNIT MOVEMENT ROUTINE */

      // If the unit is a worker and it sees at least one resource
      // then make it go to the first resource to collect it.
      if (unit.type == UnitType.WORKER && unit.resourcesInView.length == 0 && !memory.scoutedResources.isEmpty()) {
        ResourceInView closeResourcePosition = null;
        float closeResourceDistance = 9999999;
        for (ResourceInView resourcePosition : memory.scoutedResources) {
          float resourceDistance = MathUtil.distance(unit.x, unit.y, resourcePosition.x, resourcePosition.y);
          if (resourceDistance < closeResourceDistance) {
            closeResourcePosition = resourcePosition;
            closeResourceDistance = resourceDistance;
          }
        }

        if (closeResourceDistance < 80) {
          unit.navigationPath = null;
          api.navigationStart(unit.id, closeResourcePosition.x, closeResourcePosition.y);
          memory.removeScoutedResource(closeResourcePosition);
          api.saySomething(unit.id, "going to scouted!");
        }

      }
      /* WORKER ACTION ROUTINE */
      if (unit.type == UnitType.WORKER && unit.resourcesInView.length > 0 && unit.health > 20) {
        ResourceInView resource = unit.resourcesInView[0];

        //TODO: Check what to do with de memory saved data.
        if (memory.checkIfIsAlreadyInList(resource)) {
          memory.removeScoutedResource(resource);
          api.saySomething(unit.id, "scouted on sight!");

        }
        api.navigationStart(unit.id, resource.x, resource.y);

      }

      /* END OF WORKER ACTION ROUTINE */
      // If the unit is a warrior and it sees an opponent then start shooting

      /* START OF WARRIOR ACTION ROUTINE */
      if (unit.type == UnitType.WARRIOR && unit.opponentsInView.length > 0 && unit.canShoot) {
        OpponentInView opponent = unit.opponentsInView[0];
        float aimAngle = MathUtil.angleBetweenUnitAndPoint(unit, opponent.x, opponent.y);

        // Stop the unit for aiming only if is not low health
        if (unit.health > 25) {
          api.setSpeed(unit.id, Speed.NONE);
        }


        // Based on the aiming angle turn towards the opponent.
        if (aimAngle < 0) {
          api.setRotation(unit.id, Rotation.RIGHT);
        } else {
          api.setRotation(unit.id, Rotation.LEFT);
        }

        if (aimAngle > 15 || aimAngle < 15) {
          api.shoot(unit.id);
        }

        api.saySomething(unit.id, "I see you! HAHA");
      }
      if (unit.type == UnitType.WARRIOR && unit.resourcesInView.length > 0) {
        for (ResourceInView scoutedResource : unit.resourcesInView) {
          if (!memory.checkIfIsAlreadyInList(scoutedResource)) {
            memory.scoutedResources.add(scoutedResource);
            api.saySomething(unit.id, "FOUNDED RESOURCE");
          }
        }
      }

      /* END OF WARRIOR ROUTINE */
    }
  }

  // Connects your bot to Lia game engine, don't change it.
  public static void main(String[] args) throws Exception {
    NetworkingClient.connectNew(args, new MyBot());
  }
}

// TODO: To other file

//Methods to save usefull positions in memory
class CustomMemory {

  List<ResourceInView> scoutedResources = new ArrayList<ResourceInView>();


  boolean checkIfIsAlreadyInList(ResourceInView resource) {
    if (scoutedResources != null && !scoutedResources.isEmpty()) {
      return scoutedResources.stream().anyMatch(o -> o.x == resource.x && o.y == resource.y);
    }
    return false;
  }

  void removeScoutedResource(ResourceInView resource) {

    if (scoutedResources != null && !scoutedResources.isEmpty()) {
      ResourceInView resourcex = this.findScouted(resource);
      scoutedResources.remove(resourcex);
    }


  }

  ResourceInView findScouted(ResourceInView resource) {
    for (ResourceInView scoutedResource : scoutedResources) {
      if (scoutedResource.x == resource.x && scoutedResource.y == resource.y) {
        return scoutedResource;
      }
    }
    return null;
  }


}
