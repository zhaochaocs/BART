/*
 * NameStructure.java
 *
 * Created on August 15, 2007, 3:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package elkfed.nlp.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class finds internal structure in names. It is adapted from a
 * Perl script found at
 *
 * http://www.cs.utah.edu/~hal/HAPNIS/
 *
 *
 * @author ajern
 */
public class NameStructure {

//    private static String firstNames[] = {"abby", "abigail", "ada", "addie", "adela", "adele", "adeline", "adolfo", "adriana", "adrienne", "agnes", "aida", "aileen", "aimee", "aisha", "al", "alan", "alana", "alberta", "aldo", "alec", "alejandra", "alexandra", "alexandria", "alfonzo", "alfreda", "alfredo", "alice", "alicia", "aline", "alisa", "alisha", "alison", "alissa", "allyson", "alma", "alphonse", "alphonso", "alta", "althea", "alvaro", "alvin", "alyce", "alyson", "alyssa", "amalia", "amanda", "amber", "amelia", "amie", "amparo", "amy", "ana", "anastasia", "andrea", "andrew", "andy", "angela", "angelia", "angelica", "angelina", "angeline", "angelique", "angelita", "angie", "anita", "ann", "anna", "annabelle", "anne", "annette", "annie", "annmarie", "antoinette", "antonia", "antony", "antwan", "april", "araceli", "ariel", "arlene", "armando", "arnulfo", "arron", "art", "arturo", "ashlee", "audra", "audrey", "augusta", "aurelia", "aurelio", "aurora", "autumn", "ava", "barbara", "barbra", "beatrice", "beatriz", "beau", "becky", "belinda", "ben", "benita", "bennie", "bernadette", "bernadine", "bernice", "bernie", "bert", "bertha", "bertie", "beryl", "bessie", "beth", "bethany", "betsy", "bette", "bettie", "betty", "bettye", "beulah", "beverley", "bianca", "billy", "blanca", "bobbi", "bobbie", "bobby", "bonita", "bonnie", "brad", "branden", "brandi", "brandie", "brenda", "brendan", "bret", "brian", "briana", "brianna", "bridget", "bridgett", "bridgette", "brigitte", "britney", "brittany", "brittney", "bryon", "buddy", "caitlin", "caleb", "callie", "camille", "candace", "candice", "candy", "cara", "carissa", "carla", "carlene", "carly", "carmela", "carmella", "carmelo", "carmen", "carmine", "carol", "carole", "caroline", "carolyn", "carrie", "casandra", "cassandra", "cassie", "catalina", "catherine", "cathleen", "cathryn", "cathy", "cecelia", "cecile", "cecilia", "cedric", "celeste", "celia", "celina", "chad", "chandra", "charlene", "charlotte", "charmaine", "chasity", "chelsea", "cheri", "cherie", "cheryl", "chris", "christa", "christi", "christina", "christine", "christoper", "christopher", "chrystal", "chuck", "cindy", "clara", "clarence", "clarice", "clarissa", "claudette", "claudia", "claudine", "cleo", "clint", "coleen", "colette", "colleen", "concetta", "connie", "consuelo", "corina", "corine", "corinne", "cornelia", "corrine", "cristina", "crystal", "curt", "cynthia", "cyril", "daisy", "damien", "dan", "danial", "danielle", "danny", "dante", "daphne", "daren", "darin", "darius", "darla", "darlene", "darrel", "darrell", "darren", "darrin", "darryl", "daryl", "david", "dawn", "deana", "deandre", "deann", "deanna", "deanne", "debbie", "debora", "deborah", "debra", "deena", "deidre", "deirdre", "delbert", "della", "delmar", "delores", "deloris", "demetrius", "dena", "denise", "denver", "deon", "derek", "derick", "desiree", "devin", "devon", "dewayne", "diana", "diane", "diann", "dianna", "dianne", "dina", "dino", "dirk", "dixie", "dollie", "dolores", "dominic", "don", "dona", "donald", "donn", "donna", "donnie", "donny", "dora", "doreen", "dorian", "doris", "dorothea", "dorothy", "dorthy", "doug", "duane", "dustin", "dusty", "dwayne", "dylan", "earlene", "earline", "earnestine", "ebony", "ed", "eddie", "edgardo", "edith", "edmund", "edna", "eduardo", "edward", "edwardo", "edwin", "edwina", "effie", "efrain", "efren", "eileen", "elaine", "elba", "eldon", "eleanor", "elena", "eli", "elijah", "elinor", "elisa", "elisabeth", "elise", "eliseo", "eliza", "elizabeth", "ella", "ellen", "elma", "elmo", "elnora", "eloise", "eloy", "elsa", "elsie", "elva", "elvia", "elvin", "elvira", "elvis", "emil", "emile", "emilia", "emilio", "emily", "emma", "enid", "enrique", "eric", "erica", "erich", "erick", "ericka", "erik", "erika", "erin", "erma", "erna", "ernest", "ernestine", "ernesto", "ernie", "errol", "esmeralda", "esperanza", "essie", "estela", "estella", "esther", "ethan", "ethel", "etta", "eugene", "eugenia", "eugenio", "eula", "eunice", "eva", "evangelina", "evangeline", "eve", "evelyn", "ezra", "fannie", "fanny", "faustino", "faye", "felecia", "felicia", "fidel", "florine", "flossie", "fran", "frances", "francesca", "francine", "francisca", "frankie", "fred", "freddie", "freddy", "frederic", "freida", "frieda", "gabriela", "gabrielle", "gail", "galen", "gena", "genaro", "gene", "geneva", "genevieve", "geoffrey", "george", "georgette", "georgia", "georgina", "gerald", "geraldine", "gerardo", "gertrude", "gilberto", "gilda", "gina", "ginger", "gino", "giovanni", "gladys", "glenda", "glenna", "gloria", "goldie", "gonzalo", "gracie", "graciela", "greg", "greta", "gretchen", "gus", "gustavo", "gwen", "gwendolyn", "hal", "hallie", "harold", "harriet", "harriett", "hattie", "heather", "heidi", "helen", "helena", "helene", "helga", "henrietta", "heriberto", "herminia", "herschel", "hershel", "hilary", "hilda", "hillary", "hiram", "humberto", "ian", "ida", "ila", "ilene", "imelda", "imogene", "ina", "ines", "inez", "ingrid", "ira", "irene", "iris", "irma", "isabella", "isaiah", "isiah", "isidro", "ismael", "iva", "ivan", "jackie", "jacklyn", "jaclyn", "jacqueline", "jacquelyn", "jake", "jamaal", "jamal", "jame", "jamel", "jami", "jamie", "jana", "jane", "janell", "janet", "janette", "janice", "janie", "janine", "janna", "jannie", "jarred", "jarrod", "jasmine", "jason", "jayson", "jeanette", "jeanie", "jeanine", "jeanne", "jeannette", "jeannie", "jeannine", "jeff", "jeffrey", "jeffry", "jenifer", "jenna", "jennie", "jennifer", "jerald", "jeremy", "jeri", "jermaine", "jerold", "jerri", "jerrod", "jerry", "jesse", "jessica", "jesus", "jewel", "jill", "jillian", "jim", "jimmie", "jimmy", "joan", "joann", "joanna", "joanne", "joaquin", "jocelyn", "jodi", "jodie", "jody", "joe", "joel", "joesph", "joey", "johanna", "john", "johnathan", "johnathon", "johnie", "johnnie", "johnny", "jolene", "jon", "jonathan", "jonathon", "joni", "jose", "josef", "josefa", "josefina", "joseph", "josephine", "josh", "joshua", "josiah", "josie", "josue", "juan", "juana", "juanita", "judith", "judy", "julia", "juliana", "julianne", "julie", "juliet", "juliette", "julio", "june", "justin", "justine", "kaitlin", "kara", "kareem", "karen", "kari", "karin", "karina", "karla", "karyn", "kasey", "kate", "katelyn", "katharine", "katherine", "katheryn", "kathie", "kathleen", "kathrine", "kathryn", "kathy", "katie", "katina", "katrina", "katy", "kayla", "keisha", "kelli", "kellie", "kelvin", "ken", "kendra", "kenneth", "kennith", "kenya", "keri", "kermit", "kerri", "kevin", "kieth", "kimberley", "kimberly", "kirsten", "kitty", "kris", "krista", "kristen", "kristi", "kristie", "kristin", "kristina", "kristine", "kristopher", "kristy", "krystal", "kurtis", "ladonna", "lakeisha", "lakisha", "lana", "lanny", "larry", "latasha", "latisha", "latonya", "latoya", "laura", "lauren", "lauri", "laurie", "lavern", "laverne", "lavonne", "lawanda", "leah", "leann", "leanna", "leanne", "leila", "lela", "lelia", "lemuel", "lena", "lenora", "lenore", "leola", "leona", "leonel", "leonor", "leopoldo", "les", "lesa", "lessie", "leta", "letha", "leticia", "letitia", "lidia", "lila", "lilia", "lilian", "liliana", "lillian", "lily", "lina", "linda", "linwood", "lionel", "lisa", "liz", "liza", "lizzie", "lois", "lola", "lolita", "lon", "lonnie", "loraine", "loren", "lorena", "lorene", "loretta", "lori", "lorie", "lorna", "lorraine", "lorrie", "lottie", "lou", "louella", "louisa", "louise", "lourdes", "luann", "lucile", "lucille", "lucinda", "lucy", "luella", "luisa", "lula", "lupe", "lydia", "lynda", "lynette", "lynne", "lynnette", "mabel", "mable", "madeleine", "madeline", "madelyn", "madge", "magdalena", "maggie", "malinda", "mamie", "mandy", "manuela", "marc", "marcel", "marcelino", "marcella", "marci", "marcia", "marcie", "margaret", "margarita", "margarito", "margery", "margie", "margo", "margret", "marguerite", "mari", "maria", "marian", "mariana", "marianne", "maribel", "maricela", "marie", "marietta", "marilyn", "marina", "mario", "marisa", "marisol", "marissa", "maritza", "marjorie", "mark", "marla", "marlene", "marlon", "marquita", "marsha", "marta", "martha", "marva", "mary", "maryann", "maryanne", "maryellen", "marylou", "matilda", "matthew", "maude", "maura", "maureen", "mavis", "maxine", "mayra", "meagan", "megan", "meghan", "melanie", "melba", "melinda", "melisa", "melissa", "melody", "melva", "mervin", "mia", "micah", "michael", "micheal", "michele", "michelle", "mike", "milagros", "mildred", "millicent", "millie", "mindy", "minerva", "minnie", "miriam", "misty", "mitch", "mitzi", "moises", "mollie", "molly", "mona", "monica", "monique", "muriel", "myra", "myrna", "myrtle", "nadia", "nadine", "nancy", "nanette", "nannie", "naomi", "natalia", "natalie", "natasha", "nelda", "nellie", "nettie", "neva", "nichole", "nickolas", "nicole", "nigel", "nikki", "nina", "nita", "noelle", "noemi", "nola", "nona", "nora", "norbert", "norberto", "noreen", "norma", "octavio", "odessa", "ofelia", "ola", "olga", "olivia", "opal", "ophelia", "ora", "orville", "oscar", "osvaldo", "pamela", "pansy", "pat", "patrica", "patrice", "patricia", "patsy", "paula", "paulette", "pauline", "pearlie", "peggy", "penelope", "peter", "petra", "phil", "phillip", "phoebe", "phyllis", "priscilla", "quentin", "rachael", "rachel", "rachelle", "ramiro", "ramona", "randal", "randi", "randy", "raquel", "raul", "raymundo", "reba", "rebecca", "rebekah", "refugio", "reggie", "regina", "reginald", "reinaldo", "rena", "renee", "reuben", "reva", "reynaldo", "rhoda", "rhonda", "richard", "rickie", "ricky", "rigoberto", "rita", "rob", "robbie", "robby", "robert", "roberta", "robin", "robyn", "rocky", "rod", "rodney", "rodolfo", "rodrick", "rodrigo", "rogelio", "roger", "rolando", "ron", "ronald", "ronda", "ronnie", "ronny", "rory", "rosalie", "rosalind", "rosalinda", "rosalyn", "rosanna", "rosanne", "roseann", "rosella", "rosemarie", "rosemary", "rosendo", "rosetta", "rosie", "roslyn", "rowena", "roxanne", "roxie", "rusty", "ruthie", "sabrina", "sadie", "sallie", "sally", "samantha", "sammie", "sammy", "sandra", "sang", "sara", "sarah", "sasha", "saundra", "savannah", "scot", "scottie", "scotty", "sean", "selena", "selma", "serena", "sergio", "shana", "shanna", "shari", "sharlene", "sharon", "sharron", "shaun", "shauna", "shawn", "shawna", "sheena", "sheila", "shelia", "sheree", "sheri", "sherri", "sherrie", "sheryl", "socorro", "sofia", "sondra", "sonia", "sonja", "sonny", "sonya", "sophia", "sophie", "staci", "stacie", "stan", "stefan", "stefanie", "stephanie", "stephen", "steve", "steven", "stevie", "sue", "susan", "susana", "susanna", "susanne", "susie", "suzanne", "suzette", "sybil", "sydney", "tabatha", "tabitha", "tamara", "tameka", "tamera", "tami", "tamika", "tammi", "tammie", "tammy", "tamra", "tania", "tanisha", "tanya", "tara", "tasha", "ted", "teddy", "terence", "teresa", "teri", "terra", "terrance", "terrence", "terri", "terrie", "tessa", "thad", "thaddeus", "thelma", "theodore", "theresa", "therese", "theron", "tia", "tim", "timmy", "timothy", "tina", "tisha", "toby", "tommie", "tommy", "toni", "tonia", "tony", "tonya", "traci", "tracie", "trenton", "trevor", "trey", "tricia", "trina", "trisha", "trudy", "twila", "ty", "tyrone", "ulysses", "ursula", "valarie", "valeria", "valerie", "vanessa", "velma", "vern", "verna", "veronica", "vicki", "vickie", "vicky", "vilma", "violet", "virgie", "virginia", "vivian", "vonda", "wanda", "wendi", "wendy", "wilda", "wilfred", "wilfredo", "willa", "william", "willie", "wilma", "winfred", "winifred", "wm", "yesenia", "yolanda", "yvette", "yvonne", "zelma"};
    private static String firstNames[] = {"abby", "abigail", "abu", "ada", "addie", "adela", "adele", "adeline", "adolfo", "adriana", "adrienne", "agnes", "ahmed", "aida", "aileen", "aimee", "aisha", "al", "alan", "alana", "alberta", "aldo", "alec", "alejandra", "alexander", "alexandra", "alexandria", "alfonzo", "alfreda", "alfredo", "alice", "alicia", "aline", "alisa", "alisha", "alison", "alissa", "allyson", "alma", "alphonse", "alphonso", "alta", "althea", "alvaro", "alvin", "alyce", "alyson", "alyssa", "amalia", "amanda", "amber", "amelia", "amie", "amir", "amparo", "amr", "amy", "ana", "anastasia", "andrea", "andrew", "andy", "angela", "angelia", "angelica", "angelina", "angeline", "angelique", "angelita", "angie", "anita", "ann", "anna", "annabelle", "anne", "annette", "annie", "annmarie", "antoinette", "antonia", "antony", "antwan", "april", "araceli", "ariel", "arlene", "armando", "arnulfo", "arron", "art", "arturo", "ashlee", "audra", "audrey", "augusta", "aurelia", "aurelio", "aurora", "autumn", "ava", "barbara", "barbra", "beatrice", "beatriz", "beau", "becky", "belinda", "ben", "benita", "bennie", "bernadette", "bernadine", "bernice", "bernie", "bert", "bertha", "bertie", "beryl", "bessie", "beth", "bethany", "betsy", "bette", "bettie", "betty", "bettye", "beulah", "beverley", "bianca", "bill", "billy", "blanca", "bob", "bobbi", "bobbie", "bobby", "bonita", "bonnie", "brad", "branden", "brandi", "brandie", "brenda", "brendan", "bret", "brian", "briana", "brianna", "bridget", "bridgett", "bridgette", "brigitte", "britney", "brittany", "brittney", "bryon", "buddy", "caitlin", "caleb", "callie", "camille", "candace", "candice", "candy", "cara", "carissa", "carla", "carlene", "carly", "carmela", "carmella", "carmelo", "carmen", "carmine", "carol", "carole", "caroline", "carolyn", "carrie", "casandra", "cassandra", "cassie", "catalina", "catherine", "cathleen", "cathryn", "cathy", "cecelia", "cecile", "cecilia", "cedric", "celeste", "celia", "celina", "celine", "chad", "chandra", "charlene", "charlotte", "charmaine", "chasity", "chelsea", "cheri", "cherie", "cheryl", "chip", "chris", "christa", "christi", "christina", "christine", "christoper", "christopher", "chrystal", "chuck", "cindy", "claire", "clara", "clarence", "clarice", "clarissa", "clark", "claudette", "claudia", "claudine", "clay", "cleo", "clint", "coleen", "colette", "colleen", "concetta", "connie", "consuelo", "corey", "corina", "corine", "corinne", "cornelia", "corrine", "cristina", "crystal", "curt", "cynthia", "cyril", "daisy", "damien", "dan", "danial", "danielle", "danny", "dante", "daphne", "daren", "darin", "darius", "darla", "darlene", "darrel", "darrell", "darren", "darrin", "darryl", "daryl", "dave", "david", "dawn", "deana", "deandre", "deann", "deanna", "deanne", "debbie", "debora", "deborah", "debra", "deena", "deidre", "deirdre", "delbert", "della", "delmar", "delores", "deloris", "demetrius", "dena", "denise", "denver", "deon", "derek", "derick", "desiree", "devin", "devon", "dewayne", "diana", "diane", "diann", "dianna", "dianne", "dick", "dina", "dino", "dirk", "dixie", "dollie", "dolores", "dominic", "don", "dona", "donald", "donn", "donna", "donnie", "donny", "dora", "doreen", "dorian", "doris", "dorothea", "dorothy", "dorthy", "doug", "duane", "dunkin", "dustin", "dusty", "dwayne", "dylan", "earlene", "earline", "earnestine", "ebony", "ed", "eddie", "edgardo", "edith", "edmund", "edna", "eduardo", "edward", "edwardo", "edwin", "edwina", "effie", "efrain", "efren", "ehud", "eileen", "elaine", "elba", "eldon", "eleanor", "elena", "eli", "elijah", "elinor", "elisa", "elisabeth", "elise", "eliseo", "eliza", "elizabeth", "ella", "ellen", "elma", "elmo", "elnora", "eloise", "eloy", "elsa", "elsie", "elton", "elva", "elvia", "elvin", "elvira", "elvis", "emil", "emile", "emilia", "emilio", "emily", "emma", "enid", "enrique", "eric", "erica", "erich", "erick", "ericka", "erik", "erika", "erin", "erma", "erna", "ernest", "ernestine", "ernesto", "ernie", "errol", "esmeralda", "esperanza", "essie", "estela", "estella", "esther", "ethan", "ethel", "etta", "eugene", "eugenia", "eugenio", "eula", "eunice", "eva", "evangelina", "evangeline", "eve", "eveka", "evelyn", "ezra", "fannie", "fanny", "faustino", "faye", "felecia", "felicia", "fidel", "florine", "flossie", "fran", "frances", "francesca", "francine", "francisca", "frankie", "fred", "freddie", "freddy", "frederic", "freida", "frieda", "gabriela", "gabrielle", "gail", "galen", "gena", "genaro", "gene", "geneva", "genevieve", "geoffrey", "george", "georgette", "georgia", "georgina", "gerald", "geraldine", "gerardo", "gertrude", "gianluca", "gilberto", "gilda", "gillian", "gina", "ginger", "gino", "giovanni", "gladys", "glenda", "glenna", "gloria", "goldie", "gonzalo", "grace", "gracie", "graciela", "greg", "gregor", "greta", "gretchen", "guoxian", "gus", "gustavo", "gwen", "gwendolyn", "hal", "hallie", "hao", "harold", "harriet", "harriett", "hattie", "heather", "heidi", "helen", "helena", "helene", "helga", "henri", "henrietta", "heriberto", "herminia", "herschel", "hershel", "hilary", "hilda", "hillary", "hiram", "humberto", "ian", "ida", "igor", "ila", "ilene", "imelda", "imogene", "ina", "ines", "inez", "ingrid", "ira", "irene", "iris", "irma", "isabella", "isaiah", "isiah", "isidro", "ismael", "iva", "ivan", "jack", "jackie", "jacklyn", "jaclyn", "jacob", "jacqueline", "jacquelyn", "jake", "jamaal", "jamal", "jame", "jamel", "james", "jami", "jamie", "jana", "jane", "janell", "janet", "janette", "janice", "janie", "janine", "janna", "jannie", "jarred", "jarrod", "jasmine", "jason", "jayson", "jeanette", "jeanie", "jeanine", "jeanne", "jeannette", "jeannie", "jeannine", "jeb", "jeff", "jeffrey", "jeffry", "jenifer", "jenna", "jennie", "jennifer", "jerald", "jeremy", "jeri", "jermaine", "jerold", "jerri", "jerrod", "jerry", "jesse", "jessica", "jesus", "jewel", "jierong", "jill", "jillian", "jim", "jimmie", "jimmy", "joan", "joann", "joanna", "joanne", "joaquin", "jocelyn", "jodi", "jodie", "jody", "joe", "joel", "joesph", "joey", "johanna", "john", "johnathan", "johnathon", "johnie", "johnnie", "johnny", "jolene", "jon", "jonathan", "jonathon", "joni", "jose", "josef", "josefa", "josefina", "joseph", "josephine", "josh", "joshua", "josiah", "josie", "josue", "ju", "juan", "juana", "juanita", "judith", "judy", "julia", "juliana", "julianne", "julie", "juliet", "juliette", "julio", "june", "justin", "justine", "kaitlin", "kara", "kareem", "karen", "kari", "karin", "karina", "karla", "karyn", "kasey", "kate", "katelyn", "katharine", "katherine", "katheryn", "kathie", "kathleen", "kathrine", "kathryn", "kathy", "katie", "katina", "katrina", "katy", "kayla", "keisha", "kelli", "kellie", "kelvin", "ken", "kendra", "kenneth", "kennith", "kenya", "keri", "kermit", "kerri", "kevin", "kieth", "kim", "kimberley", "kimberly", "kirsten", "kitty", "kofi", "kris", "krista", "kristen", "kristi", "kristie", "kristin", "kristina", "kristine", "kristopher", "kristy", "krystal", "kurtis", "ladonna", "lakeisha", "lakisha", "lana", "lanny", "larry", "lars", "latasha", "latisha", "latonya", "latoya", "laura", "lauren", "laurent", "lauri", "laurie", "lavern", "laverne", "lavonne", "lawanda", "leah", "leann", "leanna", "leanne", "lee", "leila", "lela", "lelia", "lemuel", "lena", "lenora", "lenore", "leola", "leona", "leonel", "leonor", "leopoldo", "les", "lesa", "lessie", "leta", "letha", "leticia", "letitia", "lidia", "lila", "lilia", "lilian", "liliana", "lillian", "lily", "lina", "linda", "linwood", "lionel", "lisa", "liz", "liza", "lizzie", "lois", "lola", "lolita", "lon", "lonnie", "loraine", "loren", "lorena", "lorene", "loretta", "lori", "lorie", "lorna", "lorraine", "lorrie", "lottie", "lou", "louella", "louisa", "louise", "lourdes", "luann", "lucile", "lucille", "lucinda", "lucy", "luella", "luisa", "lula", "lupe", "lydia", "lynda", "lynette", "lynne", "lynnette", "mabel", "mable", "madeleine", "madeline", "madelyn", "madge", "magdalena", "maggie", "malinda", "mamie", "mandy", "manuel", "manuela", "marc", "marcel", "marcelino", "marcella", "marci", "marcia", "marcie", "margaret", "margarita", "margarito", "margery", "margie", "margo", "margret", "marguerite", "mari", "maria", "marian", "mariana", "marianne", "maribel", "maricela", "marie", "marietta", "marilyn", "marina", "mario", "marisa", "marisol", "marissa", "maritza", "marjorie", "mark", "marla", "marlene", "marlon", "marquita", "marsha", "marta", "martha", "marva", "mary", "maryann", "maryanne", "maryellen", "marylou", "matilda", "matthew", "maude", "maura", "maureen", "mavis", "maxine", "mayra", "meagan", "megan", "meghan", "melanie", "melba", "melinda", "melisa", "melissa", "melody", "melva", "mervin", "mia", "micah", "michael", "micheal", "michele", "michelle", "mike", "milagros", "milan", "mildred", "millicent", "millie", "mindy", "minerva", "minnie", "miriam", "misty", "mitch", "mitzi", "moises", "mollie", "molly", "mona", "monica", "monika", "monique", "muriel", "mustapha", "myra", "myrna", "myron", "myrtle", "nadia", "nadine", "nancy", "nanette", "nannie", "naomi", "nasser", "natalia", "natalie", "natasha", "nelda", "nellie", "nettie", "neva", "nichole", "nickolas", "nicole", "nidal", "nigel", "nikita", "nikki", "nina", "nita", "noelle", "noemi", "nola", "nona", "nora", "norbert", "norberto", "noreen", "norma", "octavio", "odessa", "ofelia", "ola", "olga", "olivia", "opal", "ophelia", "ora", "orville", "oscar", "osvaldo", "pamela", "pansy", "pat", "patrica", "patrice", "patricia", "patsy", "patti", "paula", "paulette", "pauline", "pearlie", "peggy", "penelope", "peter", "petra", "phil", "phillip", "phoebe", "phyllis", "priscilla", "quentin", "rachael", "rachel", "rachelle", "raed", "ralph", "ramiro", "ramona", "randal", "randi", "randy", "raquel", "raul", "raymundo", "reba", "rebecca", "rebekah", "refugio", "reggie", "regina", "reginald", "reinaldo", "rena", "renee", "reuben", "reva", "reynaldo", "rhoda", "rhonda", "richard", "rickie", "ricky", "rigoberto", "rita", "rob", "robbie", "robby", "robert", "roberta", "robin", "robyn", "rocky", "rod", "rodney", "rodolfo", "rodrick", "rodrigo", "rogelio", "roger", "rolando", "ron", "ronald", "ronda", "ronnie", "ronny", "rory", "rosalie", "rosalind", "rosalinda", "rosalyn", "rosanna", "rosanne", "roseann", "rosella", "rosemarie", "rosemary", "rosendo", "rosetta", "rosie", "roslyn", "rowena", "roxanne", "roxie", "rusty", "ruth", "ruthie", "sabrina", "saddam", "sadie", "sallie", "sally", "samantha", "sammie", "sammy", "sandra", "sang", "sara", "sarah", "sasha", "saundra", "savannah", "scot", "scottie", "scotty", "sean", "selena", "selma", "serena", "sergio", "shana", "shanna", "shari", "sharlene", "sharon", "sharron", "shaun", "shauna", "shawn", "shawna", "sheena", "sheila", "shelia", "sheree", "sheri", "sherri", "sherrie", "sheryl", "shuiyu", "socorro", "sofia", "sondra", "sonia", "sonja", "sonny", "sonya", "sophia", "sophie", "staci", "stacie", "stan", "stefan", "stefanie", "stephanie", "stephen", "steve", "steven", "stevie", "strom", "sue", "susan", "susana", "susanna", "susanne", "susie", "suzanne", "suzette", "sybil", "sydney", "tabatha", "tabitha", "tamara", "tameka", "tamera", "tami", "tamika", "tammi", "tammie", "tammy", "tamra", "tania", "tanisha", "tanya", "tao", "tara", "tasha", "ted", "teddy", "terence", "teresa", "teri", "terra", "terrance", "terrence", "terri", "terrie", "tessa", "thad", "thaddeus", "tharwat", "thelma", "theodore", "theresa", "therese", "theron", "tia", "tim", "timmy", "timothy", "tina", "tisha", "toby", "tod", "todd", "tommie", "tommy", "toni", "tonia", "tony", "tonya", "traci", "tracie", "trenton", "trevor", "trey", "tricia", "trina", "trisha", "trudy", "twila", "ty", "tyrone", "ulysses", "ursula", "valarie", "valeria", "valerie", "vanessa", "velma", "vern", "verna", "veronica", "vicki", "vickie", "vicky", "vilma", "vince", "violet", "virgie", "virginia", "vivian", "vladimir", "vonda", "wanda", "wanderley", "wendi", "wendy", "wilda", "wilfred", "wilfredo", "willa", "william", "willie", "wilma", "winfred", "winifred", "wm", "xiaotong", "yasser", "yesenia", "yolanda", "yoshiro", "yvette", "yvonne", "zelma", "zhenning"};
   private static String lastNames[] = {"Abadcova", "Abrahams", "Adams", "Adamski", "Agoglia", "Airports", "Ake", "Alabama", "Albright", "Ale", "Allen", "Alter", "Ammerman", "Amos", "Anderson", "Anthony", "Arden", "Arighi", "Arkin", "Arrington", "Arthofer", "Asman", "Associates", "Atkins", "Bacarella", "Bachmann", "Baker", "Bakker", "Baldwin", "Ball", "Baltimore", "Bard", "Barnes", "Barnett", "Bartholow", "Bartlett", "Barton", "Bauer", "Beach", "Beam", "Beckwith", "Beddall", "Beebe", "Bell", "Bellas", "Bello", "Belsan", "Belz", "Bennett", "Bernie", "Bertolotti", "Bibbi", "Birney", "Birns", "Bissett", "Black", "Bleckner", "Blodgett", "Blumenfeld", "Bobek", "Boehm", "Bogart", "Boies", "Bolling", "Bono", "Bonomo", "Boone", "Boren", "Bosket", "Bowker", "Brachfeld", "Bradlee", "Braeuer", "Brand", "Brands", "Braun", "Brawer", "Brazen", "Breeden", "Brierley", "Broderick", "Brody", "Bronston", "Brookline", "Brown", "Bruno", "Bryant", "Buckley", "Buente", "Burford", "Burgee", "Burke", "Burzon", "bush", "Bush", "Butler", "Bye", "Bynoe", "Byrne", "Califano", "Camel", "Cammack", "Campbell", "Campeau", "Cape", "Cardillo", "Carlson", "Carpenter", "Carr", "Carter", "Cash", "Castro", "Cawdron", "Cedergren", "Center", "Chaise", "Chamberlain", "Chambers", "Chandelier", "Chang", "Chernobyl", "Childs", "Chojnowski", "Christian", "Christie", "Christopher", "Chung", "Churchill", "Claiborne", "Clayson", "Cleave", "Clinton", "Cochran", "Cohen", "Colby", "Cole", "Coleman", "College", "Collins", "Combs", "Congdon", "Connors", "Conrades", "Conway", "Cook", "Coors", "Corey", "Corrigan", "court", "Covey", "Cox", "Crandall", "Crawford", "Crier", "Crosby", "Cunningham", "Curcio", "Currie", "D'Agosto", "Dale", "Dalldorf", "Dalton", "Daly", "David", "Davis", "Dean", "Deaver", "DeBakey", "DeCarlo", "Dederick", "Delaney", "DeMarco", "Dennis", "Denver", "DeVillars", "Diamond", "Dillow", "Dingell", "Dinkins", "Dominici", "Donahue", "Dongen", "Donnelly", "Donovan", "Dooling", "Dorfman", "Dow", "Drenkin", "Drexler", "Drogoul", "Droz", "Drury", "Dryja", "Duclos", "Duffield", "Dukakis", "Dunlaevy", "Early", "Eddington", "Edelman", "Egnuss", "Ehman", "Ehrlich", "Eisenberg", "Ely", "Endelman", "Enright", "Ensor", "Enzor", "Epp", "Eskridge", "Evans", "Falder", "Fearon", "Featherston", "Featherstone", "Feick", "Feltes", "Fenn", "Ferguson", "Fernandez", "Finn", "Fitzsimmons", "Fixx", "Fleming", "Flom", "Floyd", "Fonda", "Ford", "Forrester", "Fortin", "Fossan", "Fowler", "Foy", "Frankel", "Freed", "Freedman", "Fremd", "Fried", "Friedman", "Froistad", "Gabelli", "Gainen", "Galbraith", "Gargan", "Garratt", "Garrison", "Garsten", "Garvey", "Garzarelli", "Gates", "Gebhard", "Gelbart", "Gellert", "George", "Gerden", "Gerhardt", "Gershwin", "Gersony", "Gifford", "Giguiere", "Gilder", "Gillers", "Ginsburg", "Ginzburg", "Gizbert", "Glaser", "Glenn", "Goldberg", "Goldscheider", "Goldsmith", "Goldston", "Goode", "Goodman", "Goodrich", "Gordon", "Gore", "Gorman", "Gould", "Gourlay", "Granville", "Grasso", "Graves", "Gray", "Greenberg", "Greene", "Greenfield", "Greenspan", "Griffin", "Gross", "Hafer", "Hale", "Halis", "Hall", "Hamilton", "Hanauer", "Hancock", "Hart", "Harty", "Hayes", "Heinemann", "Hemingway", "Henning", "Herbaty", "Herrero", "Hershhenson", "Herwick", "Hibben", "Hill", "Hilton", "Hiltunen", "Hindle", "Hinman", "Hinton", "Hirschfeld", "Hockney", "Hoffman", "Holbrooke", "Holewinski", "Holland", "Hollis", "Honecker", "Hood", "Hordern", "Hornaday", "Howe", "Hubel", "Huber", "Hulings", "Hume", "hunters", "Hurst", "Hurwitt", "Hurwitz", "Hyeth", "Jackson", "Jacobsen", "Jakob", "James", "Jellison", "Jenkins", "Jennings", "Jenrette", "Jewell", "Joel", "Johns", "Johnson", "Jones", "Jordan", "Judd", "Juge", "Kadane", "Kahn", "Kaiser", "Kamm", "Kann", "Karches", "Karin", "Karl", "Katz", "Kaufman", "Kaul", "Kaye", "Kellan", "Kelly", "Kennedy", "Ketchum", "Kimbrough", "King", "Kinkel", "Kirk", "Kleinman", "Klensch", "Klineberg", "Kluge", "Kochis", "Koenig", "Kolb", "Konner", "Kopp", "Koppel", "Kristol", "Kriz", "Kron", "Krulwich", "Krupp", "Kummerfeld", "Kurtanjek", "Kushkin", "Kushnick", "Kwan", "Lack", "Lackey", "Lambert", "Lampe", "Lane", "Lang", "Langendorf", "Larsen", "Laurie", "Lavelle", "LaWare", "Lawrence", "Lawrenson", "Leavitt", "LeBowe", "Lederberg", "Lee", "Leibowitz", "Leish", "Leno", "Lesk", "Lesko", "Levin", "Lewinsky", "Lichtblau", "Lieb", "Lipinsky", "Lipps", "Litke", "Little", "Live", "Lloyd", "Lock", "Lodge", "Loggia", "Lombardo", "Lord", "Lowe", "Luciano", "Lund", "Lynes", "Mac", "Mack", "MacVicar", "Maddie", "Madison", "Mae", "Maeur", "Magleby", "Maguire", "Mair", "Maloney", "Mandina", "Manley", "Mark", "Markese", "Markrud", "Marshall", "Marshalll", "Martin", "Martins", "Marver", "Mason", "Matheson", "Mathews", "Mathewson", "Maxwell", "May", "McBride", "McCall", "McCarthy", "McCracken", "McDonough", "McDuffie", "McGee", "McGrath", "McIntyre", "McKenzie", "McKim", "McMahon", "McNamara", "McRee", "McWethy", "Mehl", "Melloan", "Merrill", "Michael", "Milken", "Miller", "Mills", "Minella", "Minna", "Minnelli", "Mississippi", "Mitchell", "Moos", "Moran", "Morris", "Morrison", "Morrissey", "Mosbacher", "Mosettig", "Mottram", "Mueller", "Mullins", "Muniak", "Murphy", "Murray", "Nadelmann", "Nancy", "Nash", "Negas", "Negroni", "Nelson", "Nesbitt", "Nettleton", "Nichols", "Niles", "Nixon", "Noble", "Norris", "Norwitz", "Nuggets", "Nutting", "Nye", "O'Brien", "O'Donnell", "Oldenburg", "Olds", "Oldsmobile", "O'Loughlin", "Olshan", "Oppenheimer", "Organization", "Oriani", "Orson", "Oswald", "Owen", "Page", "Pagones", "Pak", "Parker", "Parrish", "Patriarca", "Pattenden", "Patterson", "Paul", "Penn", "Perez", "Perozo", "Perry", "Persky", "Perth", "Peterson", "Petrovich", "Pfiefer", "Philippoussis", "Phillips", "Pinola", "Pitcoff", "Pitman", "Pons", "Poole", "Porter", "Potach", "Poulin", "Pound", "Quinlan", "Rahn", "Rambo", "Randol", "Raptopoulos", "Rather", "Raymond", "Reagan", "Reedy", "Reeve", "Reid", "Remic", "Renta", "Rice", "Richards", "Rifkin", "Robb", "Robertson", "Robinson", "Rodgers", "Rogers", "Rohs", "Roman", "Ronan", "Rooker", "Rooney", "Roper", "Rose", "Rosen", "Rosenthal", "Ross", "Roth", "Rowland", "Rowley", "Rubenstein", "Ruberg", "Rubin", "Rudolph", "Runkel", "Russell", "Ruth", "Ryan", "Sajak", "Sakwa", "Sala", "Salters", "Saltzburg", "Samford", "Sanders", "Saunders", "Sawyer", "Scambio", "Scheerer", "Schiavone", "Schlein", "Schmidt", "Schuman", "Secord", "Seelenfreund", "Seib", "Seidman", "Sellars", "Shaffer", "Shalala", "Shannon", "Sharp", "Sharpe", "Shea", "sheppard", "Sheppard", "Sherman", "Shine", "Shirk", "Shore", "Short", "Shrieves", "Shulman", "Siano", "Silvers", "Simmons", "Simpson", "Sindona", "Sinopoli", "Sisk", "Smith", "Snezak", "Sonnenberg", "Speech", "Spence", "Stackhouse", "Stanwick", "Stark", "Starr", "Stein", "Steinberg", "Steinbrenner", "Stengel", "Stephanopoulos", "Stern", "Stevens", "Stevenson", "Stoltz", "Stolzman", "Stone", "Stouffer", "Stretch", "Strickland", "Stroup", "Stuecker", "Stumpf", "Sukle", "Sullivan", "Superfund", "Sutherland", "Sutton", "Sylvester", "Tagg", "Taupin", "Taylor", "Teagan", "Teple", "Testa", "Thal", "Thatcher", "Thomas", "Thompson", "Threlkeld", "Thygerson", "Timbers", "Tong", "Tornquist", "Tower", "Traynor", "trimble", "Trinen", "Tripp", "Trump", "Tryon", "Turner", "Vadas", "Valentine", "Vanourek", "Vinson", "Vizas", "Vogelstein", "Volokh", "Volvo", "Voss", "Wagner", "Waldman", "Walker", "Wall", "Wallace", "Wallach", "Walters", "Watkins", "Watson", "Weinberg", "Weiner", "Weiss", "Welch", "Wells", "Wenham", "Werner", "West", "Wetherell", "White", "Whiting", "Wigglesworth", "Wilkison", "Willens", "Willey", "Williams", "Willmott", "Wisconsin", "Wolf", "Wolfson", "Wood", "Woodward", "Wooten", "Wright", "Wussler", "Wyatt", "Yang", "Yeng", "Young", "Zareyes"};
    private static ArrayList<String> firstNamesList = new ArrayList(Arrays.asList(firstNames));
    private static ArrayList<String> lastNamesList = new ArrayList(Arrays.asList(lastNames));

    public static HashMap<String, String> getNameStructure(String name) {
        String names[] = name.split(" ");

        int nNames = names.length;
        String select[] = new String[nNames];

        // Initialize the tags
        for (int i = 0; i < nNames; i++) {
            select[i] = "Out";
        }

        // First look for roles
        int low = 0;
        if (names[0].toLowerCase().matches("(mr\\.?|mrs\\.?|ms\\.?|dr\\.?|miss|sir|madam|lady|president)")) {
            select[0] = "Role";
            //if (nNames > 1)
            low = 1;
        }

        if (nNames > 1 || (nNames == 1 && low == 0)) {
            int high = nNames;
            if ((names[high-1] == "I") ||
                    (names[high-1] == "II") ||
                    (names[high-1] == "III") ||
                    (names[high-1] == "IV") ||
                    (names[high-1] == "V") ||
                    (names[high-1] == "esq") ||
                    (names[high-1] == "esq.") ||
                    (names[high-1] == "Esq") ||
                    (names[high-1] == "Esq.") ||
                    (names[high-1] == "Jr") ||
                    (names[high-1] == "Jr.") ||
                    (names[high-1] == "Sr") ||
                    (names[high-1] == "Sr.")) {
                select[high-1] = "Suffix";
                high--;
            }

            // Now go back to front
   /* IT DOESN'T WORK YET */
            /* with a given list*/
            if (high - low == 1 ){//&& lastNamesList.contains(names[low])) {
                select[low] = "Surname";
            //            } else if (high - low == 2 ) {
            } else if ((high - low == 2)){// && firstNamesList.contains(names[low].toLowerCase())) {
                select[low] = "Forename";
                select[low + 1] = "Surname";
//            } else if (high - low > 2)     
            } else if ((high - low == 2) &&
                    names[low].toLowerCase().matches("(mr\\.?|mrs\\.?|ms\\.?|dr\\.?|miss|sir|madam|lady)")) {
                select[low] = "Middle";
                select[low + 1] = "Surname";
//            } else if (high - low > 2)     
            } else if ((high - low > 2)){// && firstNamesList.contains(names[low].toLowerCase())) {
                select[low] = "Forename";
                select[high - 1] = "Surname";
                for (int i = low + 1; i < high - 1; i++) {
                    select[i] = "Middle";
                }
            }

            // now, check for links
            for (int i = 1; i < high; i++) {
                if (names[i].matches(".*-.*")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("and")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("abu")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("de")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("du")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("del")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("\'")) {
                    select[i] = "Link";
                }
                if (names[i].equalsIgnoreCase("o\'")) {
                    select[i] = "Surname";
                }
            }

            String last = "Surname";
            for (int i = high - 1; i > 0; i--) {
                if (select[i] == "Link") {
                    select[i - 1] = last;
                } else {
                    last = select[i];
                }
            }
            // starting with middle is bad
            if (select[low] == "Middle") {
                select[low] = "Forename";
                if ((low + 2 < high) && (select[low + 1] == "Link")) {
                    select[low + 2] = "Forename";
                }
            }


            // check for first names only
            if ((low == 0) && (nNames == 1) && firstNamesList.contains(names[0].toLowerCase())) {
                select[0] = "Forename";
            }

        }

        // Return result
        HashMap<String, String> result = new HashMap();
        for (int i = 0; i < nNames; i++) {
            //System.out.println("Tag: " + select[i] + " Name: " + names[i]);
            String val = names[i];
            if (result.containsKey(select[i])) {
                val = result.get(select[i]) + " " + val;
            }
            //System.out.println("Tag: " + select[i] + " Name: " + val);
            result.put(select[i], val);
        }
        return result;


    }
}
