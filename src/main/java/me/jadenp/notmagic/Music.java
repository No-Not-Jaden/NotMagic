package me.jadenp.notmagic;

import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Music {
    private Plugin plugin;
    public Music(Plugin plugin){
        this.plugin = plugin;
    }
    public void play(Player p){
        new BukkitRunnable(){
            int i = 0;
            int y = 0;
            int x = 0;
            int drum = 0;
            @Override
            public void run() {
                if (i == 0)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.C));
                if (i == 4) {
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 8)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.C));
                if (i == 10){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 14)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.C));
                if (i == 16)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.C));
                if (i == 20){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 24)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.C));
                if (i == 26){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 30)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.D));
                if (i == 32)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.B));
                if (i == 36){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 40)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.B));
                if (i == 42){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 46)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(1, Note.Tone.D));
                if (i == 48)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(0, Note.Tone.D));
                if (i == 52){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 56)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.D));
                if (i == 58){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 62)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(1, Note.Tone.A));
                if (i == 64)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.E));
                if (i == 68){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 72)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.E));
                if (i == 74){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 80)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.E));
                if (i == 84){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.D));
                }
                if (i == 88)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.E));
                if (i == 90){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(1, Note.Tone.C));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(2, Note.Tone.F));
                }
                if (i == 92)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(0, Note.Tone.D));
                if (i == 96)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.D));
                if (i == 100){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.C));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.F));
                }
                if (i == 104)
                        p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.A));
                if (i == 106){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.C));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(2, Note.Tone.F));
                }
                if (i == 110)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.sharp(0, Note.Tone.G));
                if (i == 112)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.G));
                if (i == 116){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.A));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.E));
                }
                if (i == 118)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.D));
                if (i == 120)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.G));
                if (i == 122){
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.G));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.B));
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.E));
                }
                if (i == 124)
                    p.playNote(p.getLocation(), Instrument.GUITAR, Note.natural(0, Note.Tone.D));
                if (i == 128){
                    i = -1;
                    if (drum == 0){
                        drum = 1;
                    }
                }
                i++;

                if (drum == 1){
                    if (y % 4 == 0)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(1, Note.Tone.C));
                    if (y == 8)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                    if ((y-8) % 16 == 0 && y > 8)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                    y++;
                    if (y == 112) {
                        drum = 2;
                        y = 0;
                        x = 0;
                    }
                }
                if (drum == 2){
                    if (x > 19 && x % 2 == 0)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(1, Note.Tone.C));
                    if (x > 21 && (x-20) % 8 == 0)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                    if (x == 4)
                        p.playNote(p.getLocation(), Instrument.BASS_DRUM, Note.natural(0, Note.Tone.C));
                    if (x == 20)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                    if (x == 36)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                    if (x == 44)
                        p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(0, Note.Tone.C));
                    if (x == 60)
                        x = 19;
                    y++;
                    x++;
                }
                 if (!p.isOnline())
                     this.cancel();
            }
        }.runTaskTimer(plugin, 0, 2L);
    }
}
