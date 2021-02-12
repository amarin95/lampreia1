import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lia.Api;
import lia.Bot;
import lia.Constants;
import lia.MathUtil;
import lia.NetworkingClient;
import lia.api.GameState;
import lia.api.OpponentInView;
import lia.api.Point;
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

  private static final float PICK_RESOURCES_FROM_MEMORY_AFTER = 11;
  CustomMemory memory = new CustomMemory();
  Random random = new Random();

  boolean warriorOneSentToMiddle = false;
  boolean warriorTwoSentToMiddle = false;
  boolean workerSentToMiddle = false;
  boolean workerSentToUpperLeft = false;

  void goToRandomPosition(Api api, int unitId) {
    while (true) {
      int x = (int) (Math.random() * Constants.MAP_WIDTH);
      int y = (int) (Math.random() * Constants.MAP_HEIGHT);

      // Map is a 2D array of booleans. If map[x][y] equals false it means that
      // at (x,y) there is no obstacle and we can safely move our unit there.
      if (!Constants.MAP[x][y]) {
        api.navigationStart(unitId, x, y);
        break;
      }
    }
  }
  // This method is called 10 times per game second and holds current
  // game state. Use Api object to call actions on your units.
  // - GameState reference: https://docs.liagame.com/api/#gamestate
  // - Api reference:       https://docs.liagame.com/api/#api-object
  @Override
  public void update(GameState state, Api api) {

    /* INIT ROUTINE */
    if (state.time == 0) {
      return;
    }
    /* EOF INIT ROUTINE */

    /* UNITS SPAWN ROUTINE */
    int numberOfWorkers = 0;
    for (UnitData unit : state.units) {
      if (unit.type == UnitType.WORKER) {
        numberOfWorkers++;
      }
    }

    // Spawns workers when there are less than 60% of them and the game still generates resources. Otherwise go for Warriors.
    if (numberOfWorkers / (float) state.units.length < 0.6f
        && state.time < Constants.STOP_SPAWNING_AFTER / 1.2
        && numberOfWorkers <= 10) {
      if (state.resources >= Constants.WORKER_PRICE) {
        api.spawnUnit(UnitType.WORKER);
      }
    }
    else {
      if (state.time >= Constants.STOP_SPAWNING_AFTER / 1.2
          && numberOfWorkers < 2
          && state.resources >= Constants.WORKER_PRICE
          && state.resources < Constants.WARRIOR_PRICE) {
        api.spawnUnit(UnitType.WORKER);
      } else if (state.resources >= Constants.WARRIOR_PRICE) {
        api.spawnUnit(UnitType.WARRIOR);
      }
    }
    /* EOF UNITS SPAWN ROUTINE */

    /* UNITS ACTIONS LOOP */
    Point middle = new Point(Constants.MAP_WIDTH / 2, Constants.MAP_HEIGHT / 2);
    Point upperLeft = new Point( Constants.MAP_WIDTH / 6, Constants.MAP_HEIGHT * 5 / 6);
    Point lowerRight = new Point(Constants.MAP_WIDTH * 5 / 6, Constants.MAP_HEIGHT / 6);
    for (int i = 0; i < state.units.length; i++) {
      UnitData unit = state.units[i];

      /* START OF MOVEMENT ROUTINE */
      // Units scout the map randomly only if they are not moving
      if (unit.navigationPath.length == 0) {
        if (state.time < PICK_RESOURCES_FROM_MEMORY_AFTER) {
          if (unit.type == UnitType.WORKER){
            if (!workerSentToUpperLeft && unit.x != upperLeft.x && unit.y != upperLeft.y) {
              api.navigationStart(unit.id, upperLeft.x, upperLeft.y);
              workerSentToUpperLeft = true;
            } else if (!workerSentToMiddle && unit.x != middle.x - 10 && unit.y != middle.y + 10) {
              api.navigationStart(unit.id, middle.x -10, middle.y + 10);
              api.saySomething(unit.id, "Going mid");
              workerSentToMiddle = true;
            } else {
              api.navigationStart(unit.id, lowerRight.x - 10, lowerRight.y);
            }
          }

          if (unit.type == UnitType.WARRIOR) {
            if (!warriorOneSentToMiddle && unit.x != middle.x && unit.y != middle.y){
              api.navigationStart(unit.id, middle.x, middle.y);
              warriorOneSentToMiddle = true;
            } else if (!warriorTwoSentToMiddle && unit.x != middle.x && unit.y != middle.y + 30){
              api.navigationStart(unit.id, middle.x, middle.y + 30);
              warriorTwoSentToMiddle = true;
            } else {
              api.navigationStart(unit.id, lowerRight.x, lowerRight.y);
            }
          }
        } else {
          goToRandomPosition(api, unit.id);
        }
      }
      /*END OF UNIT MOVEMENT ROUTINE */

      /* If Unit scouted, goes to memory */
      if (unit.opponentsInView.length > 0) {
        for (OpponentInView opponent : unit.opponentsInView) {
          if (!memory.checkOpponentIfIsAlreadyInList(opponent)) {
            memory.scoutedOpponents.add(opponent);
          } else {
            memory.updateScoutedOpponent(opponent);
          }
        }
      }

      /* WORKER ACTION ROUTINE */
      if (unit.type == UnitType.WORKER) {
        if (unit.resourcesInView.length == 0 && !memory.scoutedResources.isEmpty() && state.time >= PICK_RESOURCES_FROM_MEMORY_AFTER) {
          api.saySomething(unit.id, "NO RESOURCE ON SIGHT");
          ResourceInView closeResourcePosition = null;
          float closeResourceDistance = 9999999;
          for (ResourceInView resourcePosition : memory.scoutedResources) {
            float resourceDistance = MathUtil.distance(unit.x, unit.y, resourcePosition.x, resourcePosition.y);
            if (resourceDistance < closeResourceDistance) {
              closeResourcePosition = resourcePosition;
              closeResourceDistance = resourceDistance;
            }
          }

          if (closeResourceDistance < 45) {
            unit.navigationPath = null;
            api.navigationStart(unit.id, closeResourcePosition.x, closeResourcePosition.y);
            if (MathUtil.distance(unit.x, unit.y, closeResourcePosition.x, closeResourcePosition.y) < 15) {
              memory.removeScoutedResource(closeResourcePosition);
            }
          }
        }

        if (unit.resourcesInView.length > 0) {
          float closerResourceDistance = 9999999;
          ResourceInView closerResource = null;
          for (ResourceInView resourceInRange : unit.resourcesInView) {
            float resourceDistance = MathUtil.distance(unit.x, unit.y, resourceInRange.x, resourceInRange.y);
            if (resourceDistance < closerResourceDistance) {
              closerResource = resourceInRange;
              closerResourceDistance = resourceDistance;
            }
          }

          if (memory.checkResourceIfIsAlreadyInList(closerResource)) {
            memory.removeScoutedResource(closerResource);

          }
          api.navigationStart(unit.id, closerResource.x, closerResource.y);
        }
      }
      /* EOF WORKER ACTION ROUTINE */

      /* START OF WARRIOR ACTION ROUTINE */
      if (unit.type == UnitType.WARRIOR && unit.opponentsInView.length == 0) {
        OpponentInView nearOpponent = memory.checkIfHasOpponentNear(unit);
        if (nearOpponent != null && unit.health > 33) {
          api.saySomething(unit.id, "GOING TO OPPONENT ID: " + nearOpponent.id);
          api.navigationStart(unit.id, nearOpponent.x, nearOpponent.y);
          memory.removeScoutedOpponent(nearOpponent);
        }
      }
      if (unit.type == UnitType.WARRIOR && unit.opponentsInView.length > 0) {
        OpponentInView opponentWithLessHP = unit.opponentsInView[0];
        int hp = unit.opponentsInView[0].health;
        for (OpponentInView opponent : unit.opponentsInView){
          if (opponent.health < hp){
            hp = opponent.health;
            opponentWithLessHP = opponent;
          }
        }

        float aimAngle = calculateAimAngle(unit, opponentWithLessHP);

        // Based on the aiming angle turn towards the opponent.
        if (aimAngle > 0 && aimAngle < 3) {
          api.setRotation(unit.id, Rotation.SLOW_LEFT);
        }
        else if (aimAngle < 0 && aimAngle > -3) {
          api.setRotation(unit.id, Rotation.SLOW_RIGHT);
        }
        else if (aimAngle < 0) {
          api.setRotation(unit.id, Rotation.RIGHT);
        }
        else {
          api.setRotation(unit.id, Rotation.LEFT);
        }

        float angleDifference = differenceAngle(unit, opponentWithLessHP);
        if ((aimAngle < angleDifference && aimAngle >= 0) || (aimAngle > -angleDifference && aimAngle <= 0)) {
          api.shoot(unit.id);
        }

        float opponentRotationRelativeToUnit = MathUtil.angleBetweenUnitAndPoint
            (opponentWithLessHP.x, opponentWithLessHP.y, opponentWithLessHP.orientationAngle, unit.x, unit.y);

        if ((opponentRotationRelativeToUnit > 110f
            || opponentRotationRelativeToUnit < -110f)
            && opponentWithLessHP.speed == Speed.FORWARD
            && MathUtil.distance(unit.x, unit.y, opponentWithLessHP.x, opponentWithLessHP.y) > 10f
            && unit.canShoot) {
          api.setSpeed(unit.id, Speed.FORWARD);
        }
        else {
          api.setSpeed(unit.id, Speed.NONE);
        }

        api.saySomething(unit.id, "Piu piu piu");
      }
      if (unit.type == UnitType.WARRIOR && unit.resourcesInView.length > 0) {
        for (ResourceInView scoutedResource : unit.resourcesInView) {
          if (!memory.checkResourceIfIsAlreadyInList(scoutedResource)) {
            memory.scoutedResources.add(scoutedResource);
          }
        }
      }
      /* EOF WARRIOR ACTION ROUTINE */
    }
    warriorOneSentToMiddle = false;
    warriorTwoSentToMiddle = false;
    workerSentToMiddle = false;
    workerSentToUpperLeft = false;
    /* EOF UNITS ACTIONS LOOP */
  }

  public float calculateAimAngle(UnitData unit, OpponentInView opponent) {

    float velocity = 0;
    if (opponent.speed == Speed.FORWARD) velocity = Constants.UNIT_FORWARD_VELOCITY;
    else if (opponent.speed == Speed.BACKWARD) velocity = Constants.UNIT_BACKWARD_VELOCITY;

    if (velocity != 0) {
      float angle = opponent.orientationAngle;

      double velocityX = 1;
      double velocityY = 1;

      if (angle <= 90f) {
        angle = angle;
      }
      else if (angle <= 180f) {
        angle = 180f - angle;
        velocityX = -1;
      }
      else if (angle <= 270f) {
        angle = angle - 180f;
        velocityX = -1;
        velocityY = -1;
      }
      else {
        angle = -angle;
        velocityY = -1;
      }

      velocityX *= Math.cos(Math.toRadians(angle)) * velocity;
      velocityY *= Math.sin(Math.toRadians(angle)) * velocity;

      double a = Math.pow(velocityX, 2) + Math.pow(velocityY, 2) - Math.pow(Constants.BULLET_VELOCITY, 2);
      double b = 2 * (velocityX * (opponent.x - unit.x) + velocityY * (opponent.y - unit.y));
      double c = Math.pow(opponent.x - unit.x, 2) + Math.pow(opponent.y - unit.y, 2);

      double disc = Math.pow(b, 2) - 4 * a * c;

      double t1 = (-b + Math.sqrt(disc)) / (2 * a);
      double t2 = (-b - Math.sqrt(disc)) / (2 * a);
      double t;

      if (t1 < t2 && t1 > 0) {
        t = t1;
      }
      else {
        t = t2;
      }

      double aimX = t * velocityX + opponent.x;
      double aimY = t * velocityY + opponent.y;

      return (float) MathUtil.angleBetweenUnitAndPoint(unit, (float) aimX, (float) aimY);
    }

    return MathUtil.angleBetweenUnitAndPoint(unit, opponent.x, opponent.y);
  }

  public float differenceAngle(UnitData unit, OpponentInView enemy) {
    float distance = MathUtil.distance(unit.x, unit.y, enemy.x, enemy.y);
    double hipo = Math.sqrt(Math.pow((double) Constants.UNIT_DIAMETER / 2, 2) + Math.pow(distance, 2));
    return (float) Math.toDegrees(Math.asin(Constants.UNIT_DIAMETER / 2 / (float) hipo)) / 2f;
  }

  // Connects your bot to Lia game engine, don't change it.
  public static void main(String[] args) throws Exception {
    NetworkingClient.connectNew(args, new MyBot());
  }
}

// TODO: To other file

//Methods to save usefull positions in memory
class CustomMemory {

  static int SCOUTED_OPPONENT_MIN_DISTANCE = 50;

  List<ResourceInView> scoutedResources = new ArrayList<ResourceInView>();

  List<OpponentInView> scoutedOpponents = new ArrayList<OpponentInView>();


  boolean checkResourceIfIsAlreadyInList(ResourceInView resource) {
    if (scoutedResources != null && !scoutedResources.isEmpty()) {
      return scoutedResources.stream().anyMatch(o -> o.x == resource.x && o.y == resource.y);
    }
    return false;
  }

  boolean checkOpponentIfIsAlreadyInList(OpponentInView opponent) {
    if (scoutedOpponents != null && !scoutedOpponents.isEmpty()) {
      return scoutedOpponents.stream().anyMatch(o -> o.id == opponent.id);
    }
    return false;
  }

  OpponentInView checkIfHasOpponentNear(UnitData unit) {
    for (OpponentInView scoutedOpponent : scoutedOpponents) {
      if (MathUtil.distance(unit.x, unit.y, scoutedOpponent.x, scoutedOpponent.y) < SCOUTED_OPPONENT_MIN_DISTANCE) {
        return scoutedOpponent;
      }
    }
    return null;
  }

  void updateScoutedOpponent(OpponentInView opponent) {
    OpponentInView savedOpponent = findScoutedOpponent(opponent);
    if (savedOpponent != null) {
      int indexOfScoutedOpponent = scoutedOpponents.indexOf(savedOpponent);
      scoutedOpponents.set(indexOfScoutedOpponent, opponent);
    }
  }

  OpponentInView findScoutedOpponent(OpponentInView opponent) {
    for (OpponentInView scoutedOpponent : scoutedOpponents) {
      if (scoutedOpponent.id == opponent.id) {
        return scoutedOpponent;
      }
    }
    return null;
  }





  void removeScoutedResource(ResourceInView resource) {

    if (scoutedResources != null && !scoutedResources.isEmpty()) {
      ResourceInView resourcex = this.findScoutedResource(resource);
      scoutedResources.remove(resourcex);
    }


  }

  void removeScoutedOpponent(OpponentInView opponent) {

    if (scoutedOpponents != null && !scoutedOpponents.isEmpty()) {
      OpponentInView opponentx = this.findScoutedOpponent(opponent);
      scoutedOpponents.remove(opponentx);
    }


  }

  ResourceInView findScoutedResource(ResourceInView resource) {
    for (ResourceInView scoutedResource : scoutedResources) {
      if (scoutedResource.x == resource.x && scoutedResource.y == resource.y) {
        return scoutedResource;
      }
    }
    return null;
  }
}
