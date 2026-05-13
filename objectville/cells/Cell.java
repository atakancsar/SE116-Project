package objectville.cells;

public class Cell {
    private int level;
    private final char type;
    private final char form;
    private final int row, column;

    public Cell(char form, int row, int column) {
        this.form = Character.toUpperCase(form);
        this.row = row;
        this.column = column;
        this.level = 0;
        this.type = determineType(this.form);
    }

    private char determineType(char form) {
        if (form == 'H' || form == 'I' || form == 'C') {
            return 'Z';
        } else if (form == 'P' || form == 'W' || form == 'T') {
            return 'U';
        } else if (form == 'R') {
            return 'R';
        } else if (form == 'E') {
            return 'E';
        } else {
            return 'S';
        }
    }

    public String getCategoryName() {
        switch (form) {
            case 'H': case 'I': case 'C':
                return "Zone";
            case 'P': case 'W': case 'T':
                return "Utility Provider";
            case 'R':
                return "Road";
            case 'E':
                return "Empty";
            default:
                return "Service";
        }
    }

    public String getFullName() {
        switch (form) {
            case 'H': return "Housing Zone";
            case 'I': return "Industrial Zone";
            case 'C': return "Commercial Zone";
            case 'P': return "Power Plant";
            case 'W': return "Water Station";
            case 'T': return "Telecom Hub";
            case 'F': return "Fire Station";
            case 'D': return "Doctor Clinic";
            case 'S': return "School";
            case 'R': return "Road";
            case 'E': return "Empty Cell";
            default:
                throw new IllegalArgumentException("Unknown form character: " + form);
        }
    }

    public String getShortName() {
        switch (form) {
            case 'H': return "House";
            case 'I': return "Industrial";
            case 'C': return "Commercial";
            case 'P': return "Power Plant";
            case 'W': return "Water Station";
            case 'T': return "Telecom Hub";
            case 'F': return "Fire Station";
            case 'D': return "Doctor Clinic";
            case 'S': return "School";
            case 'R': return "Road";
            case 'E': return "Empty Cell";
            default:
                throw new IllegalArgumentException("Unknown form character: " + form);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public char getType() {
        return type;
    }

    public char getForm() {
        return form;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
