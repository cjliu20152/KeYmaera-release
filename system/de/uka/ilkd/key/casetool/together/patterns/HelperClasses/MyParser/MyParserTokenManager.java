// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//
/* Generated By:JavaCC: Do not edit this line. MyParserTokenManager.java */
package de.uka.ilkd.key.casetool.together.patterns.HelperClasses.MyParser;

public class MyParserTokenManager implements MyParserConstants
{
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x2L) != 0L)
            return 0;
         if ((active0 & 0xc07ffe0L) != 0L)
         {
            jjmatchedKind = 23;
            return 0;
         }
         return -1;
      case 1:
         if ((active0 & 0xc07ffe0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 1;
            return 0;
         }
         return -1;
      case 2:
         if ((active0 & 0xc07ffe0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 2;
            return 0;
         }
         return -1;
      case 3:
         if ((active0 & 0xc07ffe0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 3;
            return 0;
         }
         return -1;
      case 4:
         if ((active0 & 0xc07ffe0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 4;
            return 0;
         }
         return -1;
      case 5:
         if ((active0 & 0x407ffe0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 5;
            return 0;
         }
         return -1;
      case 6:
         if ((active0 & 0x7ffa0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 6;
            return 0;
         }
         return -1;
      case 7:
         if ((active0 & 0x7fe20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 7;
            return 0;
         }
         return -1;
      case 8:
         if ((active0 & 0x7fe20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 8;
            return 0;
         }
         return -1;
      case 9:
         if ((active0 & 0x3be20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 9;
            return 0;
         }
         return -1;
      case 10:
         if ((active0 & 0x3be20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 10;
            return 0;
         }
         return -1;
      case 11:
         if ((active0 & 0x3be20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 11;
            return 0;
         }
         return -1;
      case 12:
         if ((active0 & 0x29e20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 12;
            return 0;
         }
         return -1;
      case 13:
         if ((active0 & 0x21e20L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 13;
            return 0;
         }
         return -1;
      case 14:
         if ((active0 & 0x21620L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 14;
            return 0;
         }
         return -1;
      case 15:
         if ((active0 & 0x21620L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 15;
            return 0;
         }
         return -1;
      case 16:
         if ((active0 & 0x1620L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 16;
            return 0;
         }
         return -1;
      case 17:
         if ((active0 & 0x620L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 17;
            return 0;
         }
         return -1;
      case 18:
         if ((active0 & 0x620L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 18;
            return 0;
         }
         return -1;
      case 19:
         if ((active0 & 0x600L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 19;
            return 0;
         }
         return -1;
      case 20:
         if ((active0 & 0x600L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 20;
            return 0;
         }
         return -1;
      case 21:
         if ((active0 & 0x600L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 21;
            return 0;
         }
         return -1;
      case 22:
         if ((active0 & 0x600L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 22;
            return 0;
         }
         return -1;
      case 23:
         if ((active0 & 0x600L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 23;
            return 0;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 32:
         return jjStartNfaWithStates_0(0, 1, 0);
      case 35:
         jjmatchedKind = 24;
         return jjMoveStringLiteralDfa1_0(0x780000L);
      case 39:
         return jjStopAtPos(0, 25);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x7800L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x8000680L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x40000L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x4000100L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0x38020L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x40L);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x720L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x40L);
      case 100:
         return jjMoveStringLiteralDfa2_0(active0, 0x7800L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x8000000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x8080L);
      case 112:
         return jjMoveStringLiteralDfa2_0(active0, 0x700000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x30000L);
      case 115:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000000L);
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x7800L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x30000L);
      case 104:
         return jjMoveStringLiteralDfa3_0(active0, 0x40L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x80L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x600L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x100L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x500000L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000L);
      case 116:
         return jjMoveStringLiteralDfa3_0(active0, 0x4080020L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000L);
      case 118:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 67:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000L);
      case 72:
         return jjMoveStringLiteralDfa4_0(active0, 0x600L);
      case 77:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000L);
      case 80:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000L);
      case 83:
         return jjMoveStringLiteralDfa4_0(active0, 0x800L);
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0xc0000L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000L);
      case 99:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000L);
      case 101:
         return jjMoveStringLiteralDfa4_0(active0, 0x40L);
      case 104:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000L);
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000L);
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x80L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x20100L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x8020L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x2600L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x40a0L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x100L);
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x201000L);
      case 109:
         return jjMoveStringLiteralDfa5_0(active0, 0x40L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x4030000L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x40000L);
      case 115:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x480800L);
      case 118:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x8000000L) != 0L)
            return jjStopAtPos(5, 27);
         break;
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x101040L);
      case 100:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000000L);
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000L);
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x2c0000L);
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x10180L);
      case 111:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x2820L);
      case 115:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000L);
      case 116:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000L);
      case 118:
         return jjMoveStringLiteralDfa6_0(active0, 0x600L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x40L) != 0L)
            return jjStopAtPos(6, 6);
         else if ((active0 & 0x4000000L) != 0L)
            return jjStopAtPos(6, 26);
         break;
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x42000L);
      case 99:
         if ((active0 & 0x80000L) != 0L)
            return jjStopAtPos(6, 19);
         else if ((active0 & 0x200000L) != 0L)
            return jjStopAtPos(6, 21);
         return jjMoveStringLiteralDfa7_0(active0, 0x400000L);
      case 100:
         return jjMoveStringLiteralDfa7_0(active0, 0x10000L);
      case 101:
         return jjMoveStringLiteralDfa7_0(active0, 0x600L);
      case 103:
         return jjMoveStringLiteralDfa7_0(active0, 0x100L);
      case 104:
         return jjMoveStringLiteralDfa7_0(active0, 0x4000L);
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0x800L);
      case 110:
         return jjMoveStringLiteralDfa7_0(active0, 0x8020L);
      case 115:
         return jjMoveStringLiteralDfa7_0(active0, 0x1000L);
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x120080L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x80L) != 0L)
            return jjStopAtPos(7, 7);
         else if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(7, 8);
         break;
      case 77:
         return jjMoveStringLiteralDfa8_0(active0, 0x400L);
      case 83:
         return jjMoveStringLiteralDfa8_0(active0, 0x200L);
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x20000L);
      case 100:
         return jjMoveStringLiteralDfa8_0(active0, 0x8000L);
      case 101:
         if ((active0 & 0x100000L) != 0L)
            return jjStopAtPos(7, 20);
         break;
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0x10000L);
      case 109:
         return jjMoveStringLiteralDfa8_0(active0, 0x2020L);
      case 110:
         return jjMoveStringLiteralDfa8_0(active0, 0x40800L);
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0x4000L);
      case 115:
         return jjMoveStringLiteralDfa8_0(active0, 0x1000L);
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa9_0(active0, 0x1000L);
      case 100:
         return jjMoveStringLiteralDfa9_0(active0, 0x4000L);
      case 101:
         return jjMoveStringLiteralDfa9_0(active0, 0x402200L);
      case 103:
         return jjMoveStringLiteralDfa9_0(active0, 0x800L);
      case 105:
         return jjMoveStringLiteralDfa9_0(active0, 0x8000L);
      case 111:
         return jjMoveStringLiteralDfa9_0(active0, 0x20020L);
      case 116:
         return jjMoveStringLiteralDfa9_0(active0, 0x50000L);
      case 117:
         return jjMoveStringLiteralDfa9_0(active0, 0x400L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(9, 14);
         else if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(9, 18);
         break;
      case 70:
         return jjMoveStringLiteralDfa10_0(active0, 0x800L);
      case 100:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(9, 22);
         return jjMoveStringLiteralDfa10_0(active0, 0x20L);
      case 105:
         return jjMoveStringLiteralDfa10_0(active0, 0x10000L);
      case 108:
         return jjMoveStringLiteralDfa10_0(active0, 0x400L);
      case 110:
         return jjMoveStringLiteralDfa10_0(active0, 0x20000L);
      case 116:
         return jjMoveStringLiteralDfa10_0(active0, 0xb000L);
      case 118:
         return jjMoveStringLiteralDfa10_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa11_0(active0, 0x20000L);
      case 101:
         return jjMoveStringLiteralDfa11_0(active0, 0x2200L);
      case 105:
         return jjMoveStringLiteralDfa11_0(active0, 0x8820L);
      case 111:
         return jjMoveStringLiteralDfa11_0(active0, 0x10000L);
      case 116:
         return jjMoveStringLiteralDfa11_0(active0, 0x1400L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private final int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa12_0(active0, 0x800L);
      case 102:
         return jjMoveStringLiteralDfa12_0(active0, 0x20L);
      case 105:
         return jjMoveStringLiteralDfa12_0(active0, 0x20400L);
      case 110:
         return jjMoveStringLiteralDfa12_0(active0, 0x10000L);
      case 111:
         return jjMoveStringLiteralDfa12_0(active0, 0x8000L);
      case 114:
         return jjMoveStringLiteralDfa12_0(active0, 0x3200L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private final int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(12, 13);
         else if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(12, 16);
         break;
      case 97:
         return jjMoveStringLiteralDfa13_0(active0, 0x200L);
      case 105:
         return jjMoveStringLiteralDfa13_0(active0, 0x1020L);
      case 108:
         return jjMoveStringLiteralDfa13_0(active0, 0x800L);
      case 110:
         return jjMoveStringLiteralDfa13_0(active0, 0x8000L);
      case 112:
         return jjMoveStringLiteralDfa13_0(active0, 0x400L);
      case 116:
         return jjMoveStringLiteralDfa13_0(active0, 0x20000L);
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private final int jjMoveStringLiteralDfa13_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0);
      return 13;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(13, 15);
         break;
      case 98:
         return jjMoveStringLiteralDfa14_0(active0, 0x1000L);
      case 99:
         return jjMoveStringLiteralDfa14_0(active0, 0x20L);
      case 100:
         return jjMoveStringLiteralDfa14_0(active0, 0x800L);
      case 105:
         return jjMoveStringLiteralDfa14_0(active0, 0x20000L);
      case 108:
         return jjMoveStringLiteralDfa14_0(active0, 0x600L);
      default :
         break;
   }
   return jjStartNfa_0(12, active0);
}
private final int jjMoveStringLiteralDfa14_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0);
      return 14;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x800L) != 0L)
            return jjStopAtPos(14, 11);
         break;
      case 83:
         return jjMoveStringLiteralDfa15_0(active0, 0x200L);
      case 97:
         return jjMoveStringLiteralDfa15_0(active0, 0x20L);
      case 101:
         return jjMoveStringLiteralDfa15_0(active0, 0x400L);
      case 111:
         return jjMoveStringLiteralDfa15_0(active0, 0x20000L);
      case 117:
         return jjMoveStringLiteralDfa15_0(active0, 0x1000L);
      default :
         break;
   }
   return jjStartNfa_0(13, active0);
}
private final int jjMoveStringLiteralDfa15_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(13, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(14, active0);
      return 15;
   }
   switch(curChar)
   {
      case 73:
         return jjMoveStringLiteralDfa16_0(active0, 0x400L);
      case 110:
         return jjMoveStringLiteralDfa16_0(active0, 0x20000L);
      case 116:
         return jjMoveStringLiteralDfa16_0(active0, 0x1020L);
      case 117:
         return jjMoveStringLiteralDfa16_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(14, active0);
}
private final int jjMoveStringLiteralDfa16_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(14, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(15, active0);
      return 16;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(16, 17);
         break;
      case 98:
         return jjMoveStringLiteralDfa17_0(active0, 0x200L);
      case 101:
         return jjMoveStringLiteralDfa17_0(active0, 0x1000L);
      case 105:
         return jjMoveStringLiteralDfa17_0(active0, 0x20L);
      case 110:
         return jjMoveStringLiteralDfa17_0(active0, 0x400L);
      default :
         break;
   }
   return jjStartNfa_0(15, active0);
}
private final int jjMoveStringLiteralDfa17_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(15, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(16, active0);
      return 17;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x1000L) != 0L)
            return jjStopAtPos(17, 12);
         break;
      case 99:
         return jjMoveStringLiteralDfa18_0(active0, 0x200L);
      case 111:
         return jjMoveStringLiteralDfa18_0(active0, 0x20L);
      case 115:
         return jjMoveStringLiteralDfa18_0(active0, 0x400L);
      default :
         break;
   }
   return jjStartNfa_0(16, active0);
}
private final int jjMoveStringLiteralDfa18_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(16, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(17, active0);
      return 18;
   }
   switch(curChar)
   {
      case 108:
         return jjMoveStringLiteralDfa19_0(active0, 0x200L);
      case 110:
         return jjMoveStringLiteralDfa19_0(active0, 0x20L);
      case 116:
         return jjMoveStringLiteralDfa19_0(active0, 0x400L);
      default :
         break;
   }
   return jjStartNfa_0(17, active0);
}
private final int jjMoveStringLiteralDfa19_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(17, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(18, active0);
      return 19;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x20L) != 0L)
            return jjStopAtPos(19, 5);
         break;
      case 97:
         return jjMoveStringLiteralDfa20_0(active0, 0x600L);
      default :
         break;
   }
   return jjStartNfa_0(18, active0);
}
private final int jjMoveStringLiteralDfa20_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(18, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(19, active0);
      return 20;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa21_0(active0, 0x400L);
      case 115:
         return jjMoveStringLiteralDfa21_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(19, active0);
}
private final int jjMoveStringLiteralDfa21_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(19, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(20, active0);
      return 21;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa22_0(active0, 0x400L);
      case 115:
         return jjMoveStringLiteralDfa22_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(20, active0);
}
private final int jjMoveStringLiteralDfa22_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(20, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(21, active0);
      return 22;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa23_0(active0, 0x600L);
      default :
         break;
   }
   return jjStartNfa_0(21, active0);
}
private final int jjMoveStringLiteralDfa23_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(21, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(22, active0);
      return 23;
   }
   switch(curChar)
   {
      case 115:
         return jjMoveStringLiteralDfa24_0(active0, 0x600L);
      default :
         break;
   }
   return jjStartNfa_0(22, active0);
}
private final int jjMoveStringLiteralDfa24_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(22, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(23, active0);
      return 24;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(24, 9);
         else if ((active0 & 0x400L) != 0L)
            return jjStopAtPos(24, 10);
         break;
      default :
         break;
   }
   return jjStartNfa_0(23, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x77fffb2500000000L & l) == 0L)
                     break;
                  kind = 23;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ffffffe87ffffffL & l) == 0L)
                     break;
                  kind = 23;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, 
"\160\141\164\164\145\162\156\155\157\144\151\146\151\143\141\164\151\157\156\43", "\163\143\150\145\155\141\43", "\143\157\155\155\145\156\164\43", 
"\155\141\160\160\151\156\147\43", 
"\143\141\156\110\141\166\145\123\145\166\145\162\141\154\123\165\142\143\154\141\163\163\145\163\43", 
"\143\141\156\110\141\166\145\115\165\154\164\151\160\154\145\111\156\163\164\141\156\143\145\163\43", "\141\144\144\123\164\162\151\156\147\106\151\145\154\144\43", 
"\141\144\144\103\154\141\163\163\101\164\164\162\151\142\165\164\145\43", "\141\144\144\120\141\162\141\155\145\164\145\162\43", 
"\141\144\144\115\145\164\150\157\144\43", "\160\157\163\164\143\157\156\144\151\164\151\157\156\43", 
"\160\162\145\143\157\156\144\151\164\151\157\156\43", "\160\162\145\160\157\163\164\143\157\156\144\151\164\151\157\156\43", 
"\151\156\166\141\162\151\141\156\164\43", "\43\163\164\141\164\151\143", "\43\160\162\151\166\141\164\145", 
"\43\160\165\142\154\151\143", "\43\160\162\157\164\145\143\164\145\144", null, "\43", "\47", 
"\155\145\164\150\157\144\43", "\143\154\141\163\163\43", };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0xfffffe1L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
private ASCII_CharStream input_stream;
private final int[] jjrounds = new int[1];
private final int[] jjstateSet = new int[2];
protected char curChar;
public MyParserTokenManager(ASCII_CharStream stream)
{
   if (ASCII_CharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public MyParserTokenManager(ASCII_CharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(ASCII_CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 1; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(ASCII_CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

private final Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public final Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 13 && (0x2600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
