package parallel;

public  class Player{

    String Name;
    String Age;
    String BallControl;
    String SprintSpeed;
    String Vision;
    String SkillMoves;
    String WeakFoot;
    Integer Potential;

//   public Player(String name, int age, int ballControl, int sprintSpeed, int vision, int skillMoves, int weakFoot) {
//       Name = name;
//       Age = age;
//       BallControl = ballControl;
//       SprintSpeed = sprintSpeed;
//       Vision = vision;
//       SkillMoves = skillMoves;
//       WeakFoot = weakFoot;
//   }

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
   
   public void setPotential() {
	   if((this.BallControl.isEmpty() || this.BallControl == null)) {
		   this.BallControl = "0";
	   }
	   if((this.SkillMoves.isEmpty() || this.SkillMoves == null)) {
		   this.SkillMoves = "0";
	   }
	   if((this.SprintSpeed.isEmpty() || this.SprintSpeed == null)) {
		   this.SprintSpeed = "0";
	   }
	   if((this.Vision.isEmpty() || this.Vision == null)) {
		   this.Vision = "0";
	   }
	   if((this.WeakFoot.isEmpty() || this.WeakFoot == null)) {
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