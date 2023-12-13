package Actors;

import java.util.ArrayList;
import java.util.HashSet;

import HelperMethods.Help;
import itumulator.world.World;
import itumulator.world.Location;

public abstract class SocialAnimal extends Animal{ 
    ArrayList<SocialAnimal> pack_members;

    /**
     * Creates a new animal that extends SocialAnimal
     * @param world The world the animal is to be created in
     */
    SocialAnimal(World world) {
        super(world);
        pack_members = new ArrayList<>();
    }

    /**
     * Moves the animal to the nearest member of the pack
     * @return true if the animal was able to move, false if not (most likely due to no tiles being available or no pack members)
     */
    boolean moveToNearestMember() {
        ArrayList<SocialAnimal> awake_members = new ArrayList<>();
        for (SocialAnimal member : pack_members) {
            if (!member.getIsSleeping()) {
                awake_members.add(member);
            }
        }
        SocialAnimal nearest_member = (SocialAnimal) getNearestObject(Help.castArrayList(awake_members));
        if (nearest_member == this || nearest_member == null) {
            return false;
        }
        moveTo(nearest_member.getLocation());
        return true;
    }

    @Override // Make it so setHome() updates home for all members, but this sucks
    public boolean setHome(Home home) {
        if (home == null) { return false; }
        for (SocialAnimal member : pack_members) {
            member.setIndividualHome(home); // Very bad
        }
        return true;
    }

    @Override // Make it so all packmembers know that the individual wolf has died
    public void die() {
        notifyDeath();
        super.die();
    }

    @Override // Make it so getHp() returns the combined health of the pack
    public int getHp() {
        int pack_hp = 0;
        for (SocialAnimal member : pack_members) {
            pack_hp += member.getIndividualHp();
        }
        return pack_hp;
    }

    @Override // Make it so the baby is added to the pack
    public Animal reproduce() {
        SocialAnimal baby = (SocialAnimal) super.reproduce();
        if (baby == null) { return baby; }   
        for (SocialAnimal member : pack_members) {
            if (!member.equals(this)) { // Avoiding concurrent modifcation exception
                member.addPackMember(baby);
                baby.addPackMember(member);
            }
        }
        this.addPackMember(baby);
        baby.addPackMember(this);
        baby.addPackMember(baby);
        return baby;
    }

    @Override // Make it so food is shared between all members of the pack
    public void eat(Eatable food) {
        int energy_split = Math.round(food.consumed() / pack_members.size());
        for (SocialAnimal member : pack_members) {
            member.increaseEnergy(energy_split);
        }
    }

    @Override // So far, all SocialAnimals lives in burrows. This makes it so the animal dissapears into the burrow
    public void sleep() {
        super.sleep();
        world.remove(this);
    }

    /**
     * Returns all locations visible to the pack
     * @return ArrayList of all locations visible to the pack
     */
    ArrayList<Location> getAllLocsVisibleToPack() {
        HashSet<Location> loc_set = new HashSet<>(); // We dont want overlap
        for (SocialAnimal member : pack_members) {
            if (!member.getIsSleeping()) {
                for (Location l : member.getSurroundingTilesAsList(vision_range)) {
                    loc_set.add(l);
                }
            }
        }
        ArrayList<Location> loc_list = new ArrayList<>();
        loc_list.addAll(loc_set);
        return loc_list;
    }

    /**
     * Adds a new member to the pack
     * @param new_member The new member to be added
     */
    public void addPackMember(SocialAnimal new_member) {
        pack_members.add(new_member);
    }

    /**
     * Adds a list of new members to the pack
     * @param new_members The new members to be added
     */
    public void addPackMembers(ArrayList<SocialAnimal> new_members) { // Primarily used by Main.java
        pack_members.addAll(new_members);
    }

    /**
     * Returns the health of the individual animal
     * @return The health of the individual animal
     */
    public int getIndividualHp() {
        return current_hp;
    }

    /**
     * Makes each member of the pack mad at the animal
     * @param animal The animal to be made mad at
     */
    void makePackMadAt(Animal animal) {
        for (SocialAnimal member : pack_members) {
            member.makeMadAt(animal);
        }
    }
    
    /**
     * Makes each member of the pack afraid of the animal
     * @param animal The animal to be made afraid of
     */
    void makePackAfraidOf(Animal animal) {
        for (SocialAnimal member : pack_members) {
            member.makeAfraidOf(animal);
        }
    }

    /**
     * Makes the SocialAnimal mad at the animal
     * @param animal The animal to be made mad at
     */
    public void makeMadAt(Animal animal) {
        mad_at.add(animal);
    }

    /**
     * Makes the SocialAnimal afraid of the animal
     * @param animal The animal to be made afraid of
     */
    public void makeAfraidOf(Animal animal) {
        afraid_of.add(animal);
    }

    /**
     * Removes the SocialAnimal from the pack - used when the animal dies
     */
    private void notifyDeath() {
        for (SocialAnimal member : pack_members) {
            if (!member.equals(this)) { // Avoiding concurrent modifcation exception
                member.removeMember(this);
            }
        }
    }

    /**
     * Removes a member from the pack
     * @param member The member to be removed
     */
    public void removeMember(SocialAnimal member) {
        pack_members.remove(member);
    }

    /**
     * Uses the super implementation of setHome() to set the home of the individual animal
     * @param home The home to be set
     */
    public void setIndividualHome(Home home) { // :(
        super.setHome(home);
    }

    /**
     * Returns the pack members of the animal
     * @return An ArrayList of the pack members of the animal
     */
    public ArrayList<SocialAnimal> getPackMembers() {
        return pack_members;
    }

    /**
     * Returns the size of the pack
     * @return An integer describing the size of the pack
     */
    public int getPackSize() {
        return pack_members.size();
    }


}
