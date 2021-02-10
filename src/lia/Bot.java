package lia;

import lia.api.GameState;

public interface Bot {
    void update(GameState gameState, Api response);
}
