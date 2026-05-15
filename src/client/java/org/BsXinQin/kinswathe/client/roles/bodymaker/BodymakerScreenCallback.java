package org.BsXinQin.kinswathe.client.roles.bodymaker;

import java.util.UUID;

public interface BodymakerScreenCallback {
    void setSelectedPlayer(UUID uuid);
    void setSelectedDeathReason(String deathReason);
}