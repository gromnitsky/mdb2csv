package com.sigwait.mdb2csv;

import java.util.*;
import java.io.*;
import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.util.*;

public class App {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: " + U.argv0() + " file.mdb [table]");
            System.exit(1);
        }

        var jet = new Jet(args[0]);
        if (args.length == 1) { // just list all tables
            for (String v: jet.tables) System.out.println(v);
            System.exit(0);
        }

        if (new ArrayList<>(jet.tables).indexOf(args[1]) == -1)
            U.err("invalid table name");

        // print 1 table
        var stdout = new BufferedWriter(new OutputStreamWriter(System.out));
        try {
            ExportUtil.exportWriter(jet.db, args[1], stdout, true,
                                    null, '"', SimpleExportFilter.INSTANCE);
        } catch (IOException e) {
            U.err("csv export: " + e.getMessage());
        }
    }
}

class Jet {
    public Database db;
    public Set<String> tables;
    public Jet(String name) {
        try {
            db = DatabaseBuilder.open(new File(name));
            tables = db.getTableNames();
        } catch (IOException e) {
            U.err("db open: " + e.getMessage());
        }
    }
}

class U {
    public static String argv0() {
        var p = App.class.getPackageName().split("\\."); return p[p.length-1];
    }
    public static void err(String msg) {
        System.err.println(argv0() + " error: " + msg); System.exit(1);
    }
}
