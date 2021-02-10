import java.util.ArrayList;
import java.util.List;
import lia.Api;
import lia.Bot;
import lia.Constants;
import lia.NetworkingClient;
import lia.api.GameState;
import lia.api.ResourceInView;
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
    if (state.time == 0) {

      return;
    }
    // If you have enough resources to spawn a new warrior unit then spawn it.
    if (state.resources >= Constants.WARRIOR_PRICE) {
      api.spawnUnit(UnitType.WARRIOR);
    }

    // We iterate through all of our units that are still alive.
    for (int i = 0; i < state.units.length; i++) {
      UnitData unit = state.units[i];

      // If the unit is not going anywhere, we send it
      // to a random valid location on the map.
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

      // If the unit is a worker and it sees at least one resource
      // then make it go to the first resource to collect it.
      if (unit.type == UnitType.WORKER && unit.resourcesInView.length > 0) {
        ResourceInView resource = unit.resourcesInView[0];
        if (memory.checkIfIsAlreadyInList(resource)) {
          memory.removeScoutedResource(resource);
          api.saySomething(unit.id, "going to scouted!");
        }
        api.navigationStart(unit.id, resource.x, resource.y);

      }

      // If the unit is a warrior and it sees an opponent then start shooting
      if (unit.type == UnitType.WARRIOR && unit.opponentsInView.length > 0 && unit.canShoot) {
        api.shoot(unit.id);
        api.saySomething(unit.id, "I see you! HAHA");
      }
      if (unit.type == UnitType.WARRIOR && unit.resourcesInView.length > 0 && !memory
          .checkIfIsAlreadyInList(unit.resourcesInView[0])) {
        memory.scoutedResources.add(unit.resourcesInView[0]);
        api.saySomething(unit.id, "FOUNDED RESOURCE");
      }
    }
  }

  // Connects your bot to Lia game engine, don't change it.
  public static void main(String[] args) throws Exception {
    NetworkingClient.connectNew(args, new MyBot());
  }
}

class CustomMemory {

  List<ResourceInView> scoutedResources = new ArrayList<ResourceInView>();


  boolean checkIfIsAlreadyInList(ResourceInView resource) {
    if (scoutedResources != null && !scoutedResources.isEmpty()) {
      //  return scoutedResources.stream().filter(saved -> saved.x == resource.x && saved.y == resource.y).findFirst().isPresent();
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
