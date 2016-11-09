package it.illinois.adsc.ema.pw.ied;

import com.jacob.activeX.ActiveXComponent;

/**
 * Created by prageethmahendra on 19/1/2016.
 */
@Deprecated
public class Ied {

    public static void main(String[] args) {
        ActiveXComponent comp=new ActiveXComponent("Com.Calculation");
        System.out.println("The Library been loaded, and an activeX component been created");


    }
}
