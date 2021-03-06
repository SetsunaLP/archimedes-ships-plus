package darkevilmac.archimedes.common.api.tileentity;

import darkevilmac.archimedes.common.entity.ShipCapabilities;
import darkevilmac.movingworld.common.tile.IMovingWorldTileEntity;

public interface ITileEngineModifier extends IMovingWorldTileEntity {

    /**
     * Return the power of the tile that is added to the ePower variable in the ship's datawatcher used for speed calculations.
     *
     * @param shipCapabilities
     * @return
     */
    float getPowerIncrement(ShipCapabilities shipCapabilities);

}
