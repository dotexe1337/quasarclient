/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: DataCollector
# File: Globals
# Created by constantin at 19:27, Mär 25 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package dev.semisol.quasarclient.module.dcol;

import com.mojang.bridge.game.GameVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Globals {
    public static GameVersion gv = null;
    public static long hashseed = 0;
    public static List<String> plugins = new ArrayList<>();
    public static List<String> commands = new ArrayList<>();
    public static List<String> currentCollected = new ArrayList<>();
    public static List<String> commandsLeft = new ArrayList<>();
    public static int ticks = -1;
    public static int timeout = 0;
    public static HashMap<String, List<String>> mapping = new HashMap<>();
    public static boolean inProg = false;
    public static boolean gotMessage = false;
    public static boolean tickPassed = false;
    public static String command = "";
    public static String ip = "";
    public static int port = 0;
    public static String sbrand = "";
    public static boolean sentData = false;
    public static int completionID = 0x7ffffff1;
}
