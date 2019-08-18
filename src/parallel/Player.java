package parallel;

public  class Player{

    String Name;
    String Age;
    String BallControl;
    String SprintSpeed;
    String Vision;
    String SkillMoves;
    String WeakFoot;
    String Nationality;
	Integer Potential;
	Integer Value;

   public String getName() {
       return Name;
   }

   public void setName(String name) {
       Name = name;
   }

   public String getAge() {
       return Age;
   }

   public void setAge(String age) {
       Age = age;
   }

   public String getBallControl() {
       return BallControl;
   }

   public void setBallControl(String ballControl) {
       BallControl = ballControl;
   }

   public String getSprintSpeed() {
       return SprintSpeed;
   }

   public void setSprintSpeed(String sprintSpeed) {
       SprintSpeed = sprintSpeed;
   }

   public String getVision() {
       return Vision;
   }

   public void setVision(String vision) {
       Vision = vision;
   }

   public String getSkillMoves() {
       return SkillMoves;
   }

   public void setSkillMoves(String skillMoves) {
       SkillMoves = skillMoves;
   }

   public String getWeakFoot() {
       return WeakFoot;
   }

   public void setWeakFoot(String weakFoot) {
       WeakFoot = weakFoot;
   }
   
   public String getNationality() {
       return Nationality;
   }

   public void setNationality(String nationality) {
	   
	   Nationality = nationality;
   }
   
   
   public Integer getValue() {
	return Value;
   }

   public void setValue(Integer value) {
	Value = value;
   }

public int getPotential( ) {
	   return Potential;
   }
   
   
   public void setPotential() {
	   if((this.BallControl == null  || this.BallControl.isEmpty())) {
		   this.BallControl = "0";
	   }
	   if(( this.SkillMoves == null || this.SkillMoves.isEmpty())) {
		   this.SkillMoves = "0";
	   }
	   if((this.SprintSpeed == null || this.SprintSpeed.isEmpty())) {
		   this.SprintSpeed = "0";
	   }
	   if((this.Vision == null || this.Vision.isEmpty() )) {
		   this.Vision = "0";
	   }
	   if((this.WeakFoot == null || this.WeakFoot.isEmpty())) {
		   this.WeakFoot = "0";
	   }
	   this.Potential = (
			   Integer.valueOf(this.BallControl) + 
			   (Integer.valueOf(this.SkillMoves)* 20) + 
			   Integer.valueOf(this.SprintSpeed)  +
			   Integer.valueOf(this.Vision) + 
			   (Integer.valueOf(this.WeakFoot)* 20))/5;
	   }
   
}