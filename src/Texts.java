import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Texts {
  static String places[] = { "Plaza Bar", "Light Point Hotel",
      "Glorious Emperor Bar", "Double Star Hotel", "Southern Shield Hotel",
      "Royal Universe Hotel", "Atlantis Loch Hotel", "Windmill Hotel",
      "Coffee Hotel", "Rosewood Resort", "Slumber Hotel" };

  static String name_list[] = { "Aaron", "Adam", "Adrian", "Aiden", "Alex",
      "Alexander", "Alex", "Andrew", "Andy", "Anthony", "Tony", "Arthur", "Art",
      "Austin", "Benjamin", "Ben", "Blake", "Bobby", "Bob", "Brandon", "Brian",
      "Bruce", "Cameron", "Carl", "Charles", "Charlie", "Christopher", "Chris",
      "Cody", "Colin", "Connor", "Corey", "Craig", "Daniel", "Dan", "David",
      "Dave", "Donald", "Don", "Dylan", "Edward", "Ed", "Eric", "Elliot", "Eli",
      "Ethan", "Evan", "Frank", "Frankie", "Freddie", "Fred", "Gabriel", "Gabe",
      "Gary", "George", "Harry", "Henry", "Ian", "Isaac", "Jack", "Jackson",
      "Jacob", "Jakob", "Jake", "James", "Jamie", "Jason", "Jay", "Jeffrey",
      "Jeff", "Jeremy", "Jerry", "Gerry", "Joel", "John", "Jonathan", "Jon",
      "Jordan", "Joseph", "Josef", "Joe", "Joey", "Joshua", "Josh", "Justin",
      "Kenneth", "Ken", "Kevin", "Kyle", "Lawrence", "Larry", "Leo", "Liam",
      "Logan", "Louis", "Lucas", "Luke", "Matthew", "Matt", "Maxwell", "Max",
      "Michael", "Mike", "Nathan", "Nathaniel", "Nicholas", "Nick", "Noah",
      "Nolan", "Oscar", "Owen", "Patrick", "Pat", "Paul", "Phillip", "Phil",
      "Randy", "Richard", "Rich", "Dick", "Riley", "Robert", "Bob", "Ronald",
      "Ron", "Roy", "Ryan", "Samuel", "Sammy", "Sam", "Scott", "Sean", "Shaun",
      "Sebastian", "Stanley", "Steven", "Stephen", "Steve", "Taylor", "Theo",
      "Thomas", "Tom", "Tommy", "Timothy", "Tim", "Tristan", "Tyler", "Wayne",
      "William", "Billy" };

  static String surname_list[] = { "Cohen", "Campbell", "Nevison", "Wesley",
      "Gosden", "Hamer", "Newell", "Conduit", "Roseborough", "Beeston",
      "Makepeace", "Hutchings", "Hammond", "Chetwnyd", "Dunford", "Macfarlane",
      "Tuley", "Bawler", "Brennan", "Bal", "Lyndicam", "Briggs", "Cabdy",
      "Nott", "Sargeantson", "Dickson", "Steel", "Willis", "Wilton", "Snowball",
      "Smith", "Brown", "Jones", "Taylor", "Williams", "Harris", "White",
      "Johnson", "Hall", "Robinson", "Evans", "Jackson", "Wilson", "Walker",
      "Thompson" };

  static String title_list[] = { "Mr", "Dr", "Professor", "The Rt Revd Dr",
      "The Most Revd", "The Rt Revd", "The Revd Canon", "The Revd",
      "The Rt Revd Professor", "The Ven", "The Most Revd Dr", "Rabbi", "Canon",
      "Chief", "Reverend", "Major", "Cllr", "Sir", "Rt Hon Lord", "Rt Hon",
      "The Lord", "Viscount", "Captain", "Master", "Very Revd" };

  static Set<String> memory = new TreeSet<String>();

  static String[] fortunes = read_fortunes(); // should be initialised to the
                                              // fortunes file.

  static Random rand = new Random();

  private static String[] read_fortunes() {
    Path filePath = new File("proverbs.txt").toPath();
    Charset charset = Charset.defaultCharset();
    List<String> stringList = null;
    try {
      stringList = Files.readAllLines(filePath, charset);
    } catch (IOException e) {
      System.err.println("Server can't load proverbs.txt file");
      e.printStackTrace();
    }
    return stringList.toArray(new String[] {});
  }

  public static String choose_name(int node) {
    String retval = null;
    while (retval == null || memory.contains(retval)) {
      int firstIndex = rand.nextInt(name_list.length);
      retval = title_list[rand.nextInt(title_list.length)] + " "
          + name_list[firstIndex] + " "
          + surname_list[rand.nextInt(surname_list.length)];
    }
    memory.add(retval);
    return retval;
  }

  public static void choose_messages(List<String> texts, int count,
      boolean meetings) {
    String next = null;

    String like = "%s, %s and %s are meeting at %02d:%02d in the %s.";

    int offset = rand.nextInt(200);
    while (count > 0) {
      if (meetings) {
        // Choose three names, a time and a place.
        int firstIndex = rand.nextInt(name_list.length);
        int secondIndex = rand.nextInt(name_list.length);
        while (secondIndex == firstIndex) {
          secondIndex = (secondIndex + 1) % name_list.length;
        }
        int thirdIndex = rand.nextInt(name_list.length);
        while (thirdIndex == secondIndex || thirdIndex == firstIndex) {
          thirdIndex = (thirdIndex + 1) % name_list.length;
        }
        Formatter f = new Formatter();
        f.format(like, name_list[firstIndex], name_list[secondIndex],
            name_list[thirdIndex], rand.nextInt(10) + 1, rand.nextInt(4) * 15,
            places[rand.nextInt(places.length)]);
        next = f.toString();
        f.close();
      } else {
        next = (fortunes[(23 * count + offset) % fortunes.length]);
      }
      if (next != null && !memory.contains(next) && next.length() < 80
          && next.length() > 40) {
        texts.add(next);
        count--;
      }

    }
    return;
  }

  public static String corrupt(String text) {
    return (change_name(change_time(change_place(text))));
  }

  public static String change_name(String text) {
    return change(text, name_list, "Beelzebub");
  }

  public static String change_time(String text) {
    return text.replaceFirst("[01][0-9]:[0-4][05]", "11:59");
  }

  public static String change_place(String text) {
    return change(text, places, "Hell's Kitchen");
  }

  private static String change(String text, String[] list, String replacement) {
    for (int index = 0; index < list.length; index++) {
      int where = text.indexOf(list[index]);
      if (where >= 0) {
        return text.replace(list[index], replacement);
      }
    }
    return text;
  }

}
