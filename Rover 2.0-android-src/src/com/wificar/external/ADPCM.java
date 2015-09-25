package com.wificar.external;

/*
 * ADPCM.java
 *
 * This code was ported and updated to Java from C code provided by
 * 
 * Jack Jansen
 * Centre for Mathematics and Computer Science
 * Kruislaan 413
 * Amsterdam
 * the Netherlands
 *
 *             +31 20 592 4098      
 * Jack.Jansen@cwi.nl
 *
 * The original copyright notice accompanying the code is reproduced in 
 * full below:
 * 
 ***************************************************************************
  Copyright 1992 by Stichting Mathematisch Centrum, Amsterdam, The
  Netherlands.

						All Rights Reserved

  Permission to use, copy, modify, and distribute this software and its 
  documentation for any purpose and without fee is hereby granted, 
  provided that the above copyright notice appear in all copies and that
  both that copyright notice and this permission notice appear in 
  supporting documentation, and that the names of Stichting Mathematisch
  Centrum or CWI not be used in advertising or publicity pertaining to
  distribution of the software without specific, written prior permission.

  STICHTING MATHEMATISCH CENTRUM DISCLAIMS ALL WARRANTIES WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS, IN NO EVENT SHALL STICHTING MATHEMATISCH CENTRUM BE LIABLE
  FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT
  OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

***************************************************************************
*
* The changes made by Flagstone are distributed on an 'AS IS' basis, WITHOUT 
* WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND Flagstone HEREBY DISCLAIMS 
* ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF 
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NONINFRINGEMENT OF THIRD 
* PARTY RIGHTS.
*/

/** 
 * The ADPCM class can be used to compress PCM encoded sounds to the more 
 * efficient ADPCM format. ADPCM is a compressed format and Flash supports a 
 * modified ADPCM algorithm with compressed samples taking 2, 3, 4 or 5 bits. 
 * This results in much smaller file sizes when a movie is encoded.
 *
 * <b>Examples</b>
 *
 * The following code sample illustrates how to use the ADPCM class to add 
 * sounds to a Flash file.
 *
 * int soundId = movie.newIdentifier();
 * int compressedSize = 5;
 *
 * FSSoundConstructor soundGenerator = new FSSoundConstructor("sound.wav");
 * 
 * int channelCount = soundGenerator.getNumberOfChannels();
 * int sampleCount = soundGenerator.getSamplesPerChannel();
 * int sampleRate = soundGenerator.getSamplesRate();
 * int sampleSize = soundGenerator.getSamplesSize();
 *
 * byte[] compressedSound = ADPCM.compress(soundGenerator.getSound(), 
 *     channelCount, sampleSize, compressedSize);
 * 
 * soundGenerator.setSound(FSSound.ADPCM, channelCount, sampleCount, sampleRate, 
 *     sampleSize, compressedSound);
 *
 * movie.add(soundGenerator.defineSound(soundId));
 * movie.add(new FSStartSound(FSSound(soundId, FSSound.Start)));
 *
 */
public final class ADPCM
{
	private static final int[] StepSize =  
	{
		7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
		19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
		50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
		130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
		337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
		876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
		2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
		5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
		15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767
	};
        
	private static final int[][] DeltaTable = 
	{
		{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ -1,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ -1, -1,  2,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ -1, -1, -1, -1,  2,  4,  6,  8, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ -1, -1, -1, -1, -1, -1, -1, -1,  1,  2,  4,  6,  8, 10, 13, 16 },
	};

    /**
     * Compresses a little-endian, PCM format sound to ADPCM. This method should 
     * only be used with sounds in the FSSound.PCM format since the byte-order 
     * in FSSound.NATIVE_PCM is platform dependent.
     * 
     * @param sound an array of bytes containing the sound samples.
     * 
     * @param numberOfChannels the number of channels in the sound, typically
     * 1 (mono) or 2 (stereo).
     * 
     * @param sampleSize the size of each sound sample in bytes, typically 1 or 2.
     * 
     * @param compressedSize the number of bits to compress each sound sample to.
     * Flash supports sample sizes in the range 2..5 bits.
     * 
     * @return an array of bytes containing the compressed sound.
     */
    public static byte[] compress(byte[] sound, int numberOfChannels, int sampleSize, int compressedSize)
    {
		int samplesPerFrame = 4096;
    	
        // Calculate size of encoded data 
        
		int numberOfSamples = sound.length / sampleSize;
		int samplesPerChannel = numberOfSamples / numberOfChannels;
		int numberOfFrames = (samplesPerChannel + 4095) / 4096;
        
		int frameSize = (16 + 6 + samplesPerFrame * compressedSize) * numberOfChannels;
		int lastFrameSize = (16 + 6 + (samplesPerChannel % samplesPerFrame) * compressedSize) * numberOfChannels;
		int bytesPerFrame = (frameSize + 7) >> 3;
        
		byte[] out = new byte[numberOfFrames*bytesPerFrame];

        /*
         * Expand the sound data to 16-bit samples.
         */
        int[] samples = new int[numberOfSamples];
        
        FSCoder soundIn = new FSCoder(FSCoder.LITTLE_ENDIAN, sound);
        
        for (int i=0; i<numberOfSamples; i++)
            samples[i] = soundIn.readWord(sampleSize*8, true);

        /*
         * Allocate an initialize arrays to hold value, the index into the step
         * size table and the step size for the current sample in each channel.
         */
        int[] value = new int[numberOfChannels];
        int[] tableIndex = new int[numberOfChannels];
        int[] step = new int[numberOfChannels];

        for (int chan=0; chan<numberOfChannels; chan++)
        {
            value[chan] = 0;
            tableIndex[chan] = 0;
            step[chan] = 0;
        }

        int currentSample = 0;

        FSCoder compressedData = new FSCoder(FSCoder.LITTLE_ENDIAN, new byte[numberOfSamples*sampleSize]);

		/*
		 * ADPCM encoded sounds in Flash start with a 2-bit field contains the 
		 * number of bits per sample minus 2.
		 */
        compressedData.writeBits(compressedSize-2, 2);

        for (int i=0; i<samplesPerChannel-1; i++)
        {
            if (i % 4096 == 0) // start a new packet every 4096 samples
            {
				compressedData.alignToByte();
            	
                for (int chan=0; chan<numberOfChannels; chan++, currentSample++)
                {
                    value[chan] = samples[currentSample];
                    
                    int diff = Math.abs(samples[currentSample+numberOfChannels] - value[chan]);

                    // Calculate initial index & step

                    int index = 0;

                    while (StepSize[index] < diff && index < 63)
                        index ++;

                    tableIndex[chan] = index;
                    step[chan] = StepSize[index];

                    // Write initial index into StepSizeTable
                    
                    compressedData.writeBits(value[chan], 16);
                    compressedData.writeBits(tableIndex[chan], 6);
                }
            }
            else
            {
                for (int chan=0; chan<numberOfChannels; chan++, currentSample++)
                {
                    // Step 1 - compute difference with previous value
                    
                    int diff = samples[currentSample] - value[chan];
                    int sign = (diff < 0) ? (1 << (compressedSize-1)) : 0;

                    if (sign > 0) diff = (-diff);

                    // Step 2 - Divide and clamp
                    int delta  = 0;
                    int vpdiff = step[chan] >> (compressedSize-1);

                    for (int j=compressedSize-2; j>=0; j--, step[chan] >>= 1)
                    {
                        if (diff >= step[chan])
                        {
                            delta = delta | (1 << j);
                            vpdiff += step[chan];
                            
                            if (j > 0)
                                diff -= step[chan];
                        }
                    }

                    // Step 3 - Update previous value
                    if (sign > 0)
                        value[chan] -= vpdiff;
                    else
                        value[chan] += vpdiff;

                    // Step 4 - Clamp previous value to 16 bits
                    if (value[chan] > 32767) value[chan] = 32767;
                    if (value[chan] < -32768) value[chan] = -32768;

                    // Step 5 - Assemble value, update index and step values

                    tableIndex[chan] += DeltaTable[compressedSize][delta];

                    // Clamp StepSizeTable index
                    if (tableIndex[chan] < 0) tableIndex[chan] = 0;
                    if (tableIndex[chan] > 88) tableIndex[chan] = 88;
                    
                    step[chan] = StepSize[tableIndex[chan]];

                    // Step 6 - Output value
                    compressedData.writeBits(delta |= sign, compressedSize);
                }
            }
        }

        int compressedLength = compressedData.getPointer() / 8;

        for (int i=0; i<compressedLength; i++)
            out[i] = compressedData.getData()[i];

		return out;
	}
}
