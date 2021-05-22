package weapon;

import javafx.util.Pair;
import utility.ResourceManager.ImageResource;
import utility.ResourceManager.ItemResource;
import utility.ResourceManager.SoundResource;

public class DesertEagle extends Gun {

	public DesertEagle(int team, int holderZIndex) {
		super(ImageResource.GUN_DESERTEAGLE, 1, 1, ImageResource.BULLET, 10, 10, 10, team, holderZIndex);
		this.name = "Desert Eagle";
		this.description = String.format("%d damage, %.1f second firerate", attackDamage, 1 / attackSpeed);
		this.cost = 30;
		this.attackSound = SoundResource.GUN_DESERTEAGLE;
		this.attackSoundVolume = 0.5;
		this.itemResource = ItemResource.GUN_DESERTEAGLE;
		this.bulletUse = ItemResource.AMMO_PISTOL;
		itemOnBuy.add(new Pair<>(this.itemResource, 1));
		itemOnBuy.add(new Pair<>(this.bulletUse, 30));
	}

}