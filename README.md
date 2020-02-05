<html>

<h1>Mario AI</h1>

<h3>Overview</h3>
<p>The MarioAI project aims to allow school and university students to learn artificial intelligence in a entertaining context. Students merely require a Java IDE or their choice and the MarioAI .jar file that is produced with the maven build. Students can then use the provided API to implement an AI agent. A simple rules-based algorithm can easily be created within 10 minutes.
</p>	

<h3>Setup</h3>

<h4>Setting up the IDE<h4>
<p>Download the marioAI-&lt;version&gt;.jar from the repository. Create a new project in the IDE of your choice and include the MarioaAI.jar as 
a project dependency.</p>

<h5>Eclipse</h5>
<p>In Eclipse this can be achieved by right-clicking on the project &rarr; build path &rarr; Java build path.
In the libraries tab click on "Add External JARs" and select the marioAI-&lt;version&gt;.jar</p>

<h5>IntelliJ IDEA</h5>
<p>In the menu click File &rarr; Project Structure &rarr; Libraries &rarr; "+" and select the marioAI-&lt;version&gt;.jar</p>

<h4>Creating a Basic Agent</h4>
<p>Create a new class that extends the Class MarioAiAgent. The MarioAiAgent class specifies two abstract methods which
need to be implemented as can be seen in the snippet below.</p>

```
public class Demo extends MarioAiAgent {

    @Override
    public String getName() {
        return "Demo Agent";
    }

    @Override
    public MarioInput doAiLogic() {
        moveRight();
        return getMarioInput();
    }

    public static void main(String[] args) {
	MarioAiRunner runner = new MarioAiRunnerBuilder()
		.addAgent(new Demo())
		.setLevelConfig(LevelConfig.LEVEL_1)
		.construct();

    	runner.run();
}
```

<p>Add a main method as shown in the snippet and start MarioAI by running the method from your IDE. A window should open which show Mario walking right in a flat level.</p>

<h3>MarioAI</h3>

<h4>User Interface</h4>
<p>The user interface shows information on the current state of the game which can be useful to observe, including the 
current score as shown in the bottom left hand corner. The size of the window can be increased or decreased 
 by pressing the +/- keys. To reset the window size, press #. More functions that can be accessed can be found the the chapter "Keyboard Functions" below.
</p>
<img src="https://i.imgur.com/yODTArt.png" width="70%"/>

<h4>Keyboard Functions</h4>
<p>
While MarioAI is running you can trigger the following functions from your keyboard
<table>
    <tr>
        <th>Function/Toggle</th>
        <th>Key</th>
    </tr>
    <tr>
        <td>Increase window size</td>
        <td>+</td>
    </tr>
    <tr>
        <td>Decrease window size</td>
        <td>-</td>
    </tr>
    <tr>
        <td>Reset window size</td>
        <td>#</td>
    </tr>
    <tr>
        <td>Increase FPS</td>
        <td>j</td>
    </tr>
    <tr>
        <td>Decrease FPS</td>
        <td>k</td>
    </tr>
    <tr>
        <td>Reset FPS to default (24)</td>
        <td>l</td>
    </tr>
    <tr>
        <td>Toggle pause</td>
        <td>p</td>
    </tr>
    <tr>
        <td>Pause and advance to next frame (i.e. <i>tick()</i>)</td>
        <td>t</td>
    </tr>
    <tr>
        <td>Toggle path visualisation</td>
        <td>o</td>
    </tr>
    <tr>
        <td>Pause game and show current level state in console</td>
        <td>i</td>
    </tr>
    <tr>
        <td></td>
        <td></td>
    </tr>
</table>
</p>

<h3>MarioAI API</h3>
<p>The MarioAI API is quite extensive. The following three chapters "Controlling Mario", "Reading the Environment" and 
"Configuration" will provide an overview of the most important aspects of the API. For more information, please refer to the 
JavaDocs provided in the jar file.</p>

<h4>Controlling Mario</h4>
<p>Controlling Mario is fairly straightfoward. There is a method that can be called for each action mario can carry out (i.e. 
move right, move left, jump and sprint/shoot fireballs. Please note that the last two actions are mapped to the same button in the game, as in the original game.
To carry out one or more actions, simply call the associated method in the API
<table>
    <tr>
        <th>Action</th>
        <th>Corresponding API method</th>
    </tr>
    <tr>
        <td>Move right</td>
        <td><i>moveRight()</i></td>
    </tr>
    <tr>
        <td>Move left</td>
        <td><i>moveLeft()</i></td>
    </tr>
    <tr>
        <td>Jump</td>
        <td><i>jump()</i></td>
    </tr>
    <tr>
        <td>Sprint</td>
        <td><i>sprint()</i></td>
    </tr>
    <tr>
        <td>Shoot</td>
        <td><i>shoot()</i></td>
    </tr>
</table>
The <i>doAiLogic()</i> method must return a <i>MarioInput</i> object. By calling the aforementioned methods you can set the actions
to perform in an existing <i>MarioInput</i> object and return it with <i>getMarioInput()</i> as can bee seen below (ex. Mario will sprint right and jump)

```
@Override
public MarioInput doAiLogic() {
    moveRight();
    sprint();
    jump();
    return getMarioInput();
}
```

</p>

<h4>Reading the environment</h4>
<p>
Reading Mario's environment via the MarioAI API can be achieved either via convinience methods or via more advanced methods. Let's begin by looking at the
convienience methods provided by MarioAI which allow for quickly creating a simple rules-based AI algorithm (i.e. if this, do that). 
<table>
    <tr>
        <th>Environment Query</th>
        <th>Method</th>
    </tr>
    <tr>
        <td>Is there a brick (i.e. unpassable object) ahead?</td>
        <td><i>isBrickAhead()</i></td>
    </tr>
    <tr>
        <td>Is there an enemy ahead?</td>
        <td><i>isEnemyAhead()</i></td>
    </tr>
    <tr>
        <td>Is there a deep slope ahead?</td>
        <td><i>isDeepSlopeAhead()</i></td>
    </tr>
    <tr>
        <td>Is Mario falling (i.e. has a positive y-vector)</td>
        <td><i>isFalling()</i></td>
    </tr>
    <tr>
        <td>Is there a hole ahead (i.e. a hole which will kill Mario)?</td>
        <td><i>isHoleAhead()</i></td>
    </tr>
    <tr>
        <td>Is there a question brick above Mario?</td>
        <td><i>isQuestionbrickAbove()</i></td>
    </tr>
</table>
</p>

<h4>Running &amp; Configuration</h4>
<p>You can run your Agent by using the <i>MarioAiRunner</i> class. The simplest way of
obtaining a Runner to run your agent is using the builder <i> MarioAiRunnerBuilder </i>. You can specify different parameters with the corresponding methods.
For example you can pass an instance of your agent to the builder object with <i>addAgent()</i>.
Then you can construct a <i>MarioAiRunner</i> instance with the builder and the method <i>construct()</i>. 
By calling the method <i>run()</i> of the newly created <i>MarioAiRunner</i>, you can start a run with your agent. 
<p>MarioAI offers many ways of customizing the level for your purposes. The simplest way to configure the game is to 
simply use preconfigured levels provided in the enums provided in <i>LevelConfig</i> and set it as used level of the builder with <i>setLevelConfig()</i>.</p>

```
    public static void main(String[] args) {
	MarioAiRunner runner = new MarioAiRunnerBuilder()
		.addAgent(new Demo())
		.setLevelConfig(LevelConfig.LEVEL_1)
		.construct();
    	
    	runner.run();
    }
```

<p>
If you would like to specify your own level (including the type of level, enemies, difficulty, etc. )
you can use the many paramenters that can be passed to the constructor of 
<i>LevelConfig</i> described below
<table>
    <tr>
        <th>Parameter</th>
        <th>Usage</th>
    </tr>
    <tr>
        <td>seed</td>
        <td>The seed of the generated level layout. Using the same seed will result in the same level layout.</td>
    </tr>
    <tr>
        <td>presetDiffulty</td>
        <td>A value which specifies the difficulty by influencing the amount of enemies which are spawned.</td>
    </tr>
    <tr>
        <td>type</td>
        <td>Sets the type of level (i.e. OVERGROUND, UNDERGROUND, CASTLE)</td>
    </tr>
    <tr>
        <td>enemies</td>
        <td>Toggles enemies</td>
    </tr>
    <tr>
        <td>bricks</td>
        <td>Toggles bricks</td>
    </tr>
    <tr>
        <td>coins</td>
        <td>Toggles coins</td>
    </tr>
    <tr>
        <td>length</td>
        <td>The length of the level.</td>
    </tr>
    <tr>
        <td>odds</td>
        <td>Array with length of 5, determines the percentage of level parts [STRAIGHT, HILLS, TUBES, HOLES, BULLETBILL]</td>
    </tr>
</table>
</p>

<h3>Evaluation</h3>
<p>You can evaluate how well your agent performed in the level by either reading the score at the end of the level from the UI,
 the console or the logs. Alternatively you can read the current score from the API using the <i>getActualScore()</i> method within the agent.</p>
<p>The following table shows all rewards and penalties that can be accrued during the level.
<table>
    <tr>
        <th>Event or Condition</th>
        <th>Reward/Penalty</th>
    </tr>
    <tr>
        <td>Reach the end of the level</td>
        <td>+1024 points</td>
    </tr>
    <tr>
        <td>Remaining time at the end of the level</td>
        <td>+8 points per second left</td>
    </tr>
    <tr>
        <td>Distance passed</td>
        <td>+1 point per distance unit</td>
    </tr>
    <tr>
        <td>Collecting coins</td>
        <td>+16 per coin</td>
    </tr>
    <tr>
        <td>Defeated enemies</td>
        <td>+42 per enemy</td>
    </tr>
    <tr>
        <td>... by stomping</td>
        <td>+12 points</td>
    </tr>
    <tr>
        <td>... by hitting with a shell</td>
        <td>+17 points</td>
    </tr>
    <tr>
        <td>... by fireball</td>
        <td>+4 points</td>
    </tr>
    <tr>
        <td>Collecting non-coin items (i.e. fire-flowers, mushrooms)</td>
        <td>+58 per item</td>
    </tr>
    <tr>
        <td>Getting hit by enemy or projectile</td>
        <td>-42 per hit</td>
    </tr>
</table>
</p>

</html>
