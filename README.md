uttt-engine
============

The engine for the Ultimate Tic Tac Toe competition at TheAIGames.com

This engine has been adapted from the Connect Four engine.

Note that this does *not* include the visualizer.

To compile (Windows):

    cd [project folder]
    dir /b /s *.java>sources.txt
    md classes
    javac -d classes @sources.txt
    del sources.txt

To compile (Linux):

    cd [project folder]
    mkdir bin/
    javac -d bin/ `find ./ -name '*.java' -regex '^[./A-Za-z0-9]*$'`
    
To run:

    cd [project folder]
    java -cp bin com.theaigames.uttt.UTTT "[your bot1]" "[your bot2]" 2>err.txt 1>out.txt

[your bot1] and [your bot2] could be any command for running a bot process. For instance "java -cp /home/dev/starterbot/bin/ main.BotStarter" or "node /home/user/bot/Bot.js"

Errors will be logged to err.txt, output dump will be logged to out.txt. You can edit the saveGame() method in the AbstractGame class to output extra stuff like your bot dumps. If you want to quickly run the engine from Eclipse, change `DEV_MODE = false` to `DEV_MODE = true` in the main method of the UTTT class and provide your own bot in that method as well.

Sample command: java -cp bin com.theaigames.uttt.UTTT "java -cp D:\\Users\\Kenneth\\workspace\\UTTTBot\\bin com.kayblitz.uttt.BotStarter" "java -cp D:\\Users\\Kenneth\\workspace\\UTTTBot\\bin com.kayblitz.uttt.BotStarter"
