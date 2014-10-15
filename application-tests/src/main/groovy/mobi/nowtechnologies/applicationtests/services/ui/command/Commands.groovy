package mobi.nowtechnologies.applicationtests.services.ui.command
import mobi.nowtechnologies.applicationtests.services.ui.WebPage
import org.junit.Assert

class Commands {
    List<Command> commands = [];

    void add(Command command) {
        commands.add(command)
    }

    void add(Command ... command) {
        commands.addAll(command)
    }

    void play(WebPage page) {
        int step = 0;
        commands.each {c->
            def result = c.process(page)

            Assert.assertTrue(
                    "Did not succeed for command: (" + c + "), on step: (" + step + ") from (" + (commands.size() - 1) +
                    ") run:\n" + proccessed(commands, step) +
                    " left:\n" + left(commands, step),
                    result);

            step++;
        }
    }

    String proccessed(List<Command> commands, int index) {
        commands.subList(0, index).join("\n")
    }

    String left(List<Command> commands, int index) {
        commands.subList(index, commands.size()).join("\n")
    }

    void clear() {
        commands.clear();
    }
}
