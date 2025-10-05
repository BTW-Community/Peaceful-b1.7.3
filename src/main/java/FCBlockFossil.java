import net.minecraft.src.Block;
import net.minecraft.src.BlockStone;
import net.minecraft.src.Item;

import java.util.Random;

public class FCBlockFossil extends BlockStone {
    public FCBlockFossil(int i) {
        super(i, 0);
        setHardness(1.5F);
        setResistance(10.0F);
        setStepSound(Block.soundStoneFootstep);
        setBlockName("fcFossil");
    }

    public int idDropped(int i, Random random) { return Item.bone.shiftedIndex; }

    public int quantityDropped(Random random) {
        return 1 + random.nextInt(3);
    }
}
