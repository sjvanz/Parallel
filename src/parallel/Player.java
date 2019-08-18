package parallel;

public  class Player{

    String Name;
    String Age;
    private String BallControl;
    private String SprintSpeed;
    private String Vision;
    private String SkillMoves;
    private String WeakFoot;
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

   void setBallControl(String ballControl) {
       BallControl = ballControl;
   }

   void setSprintSpeed(String sprintSpeed) {
       SprintSpeed = sprintSpeed;
   }

   public String getVision() {
       return Vision;
   }

   public void setVision(String vision) {
       Vision = vision;
   }

   void setSkillMoves(String skillMoves) {
       SkillMoves = skillMoves;
   }

   void setWeakFoot(String weakFoot) {
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
   
   
   void setPotential() {
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
			   Integer.parseInt(this.BallControl) +
			   (Integer.parseInt(this.SkillMoves)* 20) +
			   Integer.parseInt(this.SprintSpeed)  +
			   Integer.parseInt(this.Vision) +
			   (Integer.parseInt(this.WeakFoot)* 20))/5;
	   }
   
}