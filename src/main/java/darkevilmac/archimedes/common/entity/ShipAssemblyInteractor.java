package darkevilmac.archimedes.common.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.handler.ConnectionHandler;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import darkevilmac.archimedes.common.tileentity.TileEntitySecuredBed;
import darkevilmac.movingworld.common.chunk.LocatedBlock;
import darkevilmac.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.common.chunk.assembly.AssembleResult;
import darkevilmac.movingworld.common.chunk.assembly.CanAssemble;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ShipAssemblyInteractor extends MovingWorldAssemblyInteractor {

    private int balloonCount;

    public ShipAssemblyInteractor() {
    }

    @Override
    public void toByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(getBalloonCount());
    }

    @Override
    public MovingWorldAssemblyInteractor fromByteBuf(byte resultCode, ByteBuf buf) {
        if (resultCode == AssembleResult.RESULT_NONE) {
            return new ShipAssemblyInteractor();
        }
        int balloons = buf.readInt();

        ShipAssemblyInteractor assemblyInteractor = new ShipAssemblyInteractor();
        assemblyInteractor.setBalloonCount(balloons);

        return assemblyInteractor;
    }

    @Override
    public MovingWorldAssemblyInteractor fromNBT(NBTTagCompound tag, World world) {
        ShipAssemblyInteractor mov = new ShipAssemblyInteractor();
        mov.setBalloonCount(tag.getInteger("balloonCount"));
        return mov;
    }

    @Override
    public void blockAssembled(LocatedBlock locatedBlock) {
        if (ArchimedesShipMod.instance.getNetworkConfig().isBalloon(locatedBlock.blockState.getBlock())) {
            balloonCount++;
        }
    }

    @Override
    public void blockDisassembled(LocatedBlock locatedBlock) {
        super.blockDisassembled(locatedBlock); // Currently unimplemented but leaving there just in case.

        if (locatedBlock.tileEntity != null && locatedBlock.tileEntity.getWorld() != null && !locatedBlock.tileEntity.getWorld().isRemote) {
            if (locatedBlock.tileEntity instanceof TileEntitySecuredBed) {
                TileEntitySecuredBed securedBed = (TileEntitySecuredBed) locatedBlock.tileEntity;

                securedBed.doMove = true;
                ConnectionHandler.playerBedMap.remove(securedBed.playerID);
                securedBed.addToConnectionMap(securedBed.playerID);
                securedBed.moveBed(locatedBlock.blockPos);
            }
        }
    }

    @Override
    public boolean isBlockMovingWorldMarker(Block block) {
        if (block != null)
            return block.getUnlocalizedName() == ArchimedesObjects.blockMarkShip.getUnlocalizedName();
        else
            return false;
    }

    @Override
    public boolean isTileMovingWorldMarker(TileEntity tile) {
        if (tile != null)
            return tile instanceof TileEntityHelm;
        else
            return false;
    }

    @Override
    public CanAssemble isBlockAllowed(World world, Block block, BlockPos pos) {
        CanAssemble canAssemble = super.isBlockAllowed(world, block, pos);

        if (block == ArchimedesObjects.blockStickyBuffer)
            canAssemble.assembleThenCancel = true;

        return canAssemble;
    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    @Override
    public void writeNBTFully(NBTTagCompound compound) {
        writeNBTMetadata(compound);
    }

    @Override
    public void writeNBTMetadata(NBTTagCompound compound) {
        compound.setInteger("balloonCount", getBalloonCount());
    }
}
