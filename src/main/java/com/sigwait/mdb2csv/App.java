package com.sigwait.mdb2csv;

import java.util.*;
import java.io.*;
import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.util.*;
import org.apache.commons.cli.*;

public class App {
    public static void main(String[] args) {
        var opt = new Opt(args); args = opt.cmd.getArgs();
        if (args.length == 0) { opt.usage(); System.exit(1); }

        var jet = new Jet(args[0], opt.cmd.getOptionValue("p", null));
        if (args.length == 1) { // just list all tables
            jet.tables.forEach(v -> {
                    if (!opt.cmd.hasOption("a") && jet.is_table_linked(v))
                        return;
                    System.out.println(v);
                });
            System.exit(0);
        }

        if (!jet.tables.contains(args[1])) U.err("invalid table name");

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

class Opt {
    CommandLine cmd;
    Options opt;
    Opt(String[] args) {
        opt = new Options();
        opt.addOption("a", false, "print linked tables too");
        opt.addOption("p", null, true, "password");
        try {
            cmd = new DefaultParser().parse(opt, args);
        } catch (ParseException e) {
            usage();
            U.err(e.getMessage());
        }
    }
    void usage() {
        var pw = new PrintWriter(System.err, true);
        new HelpFormatter().printHelp(pw, 74, U.argv0() + " file.mdb [table]",
                                      null, opt, 1, 3, null, true);
    }
}

class Jet {
    Database db;
    Set<String> tables;
    Jet(String name, String password) {
        try {
            db = CryptCodecUtil.setProvider(new DatabaseBuilder(new File(name))
                                            .setReadOnly(true), password)
                .open();
            tables = db.getTableNames();
        } catch (com.healthmarketscience.jackcess.impl.UnsupportedCodecException
                 | InvalidCredentialsException | IOException e) {
            U.err("db open: " + e.getMessage());
        }
    }
    Boolean is_table_linked(String name) {
        try {
            return this.db.getTableMetaData(name).isLinked();
        } catch (IOException e) {
            U.err(e.getMessage());
        }
        return false;
    }
}

class U {
    static String argv0() {
        var p = App.class.getPackageName().split("\\."); return p[p.length-1];
    }
    static void err(String msg) {
        System.err.printf("%s error: %s\n", argv0(), msg); System.exit(1);
    }
}
