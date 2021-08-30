using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Security.Cryptography;

namespace BruteForce
{
    class Program
    {

        static int baseNum;
        static string alphabet;

        static long start = 0;
        static long end = 0;


        // preformance value
        static long numToHash;
        static long previousNumToHash;
        static double speed;
        static bool ansFound = false;

        static void Main(string[] args)
        {
            //config
            int length = 6;
            string hashed = "1b4c9133da73a711322404314402765ab0d23fd362a167d6f0c65bb215113d94";
            alphabet = "abcdefghijklmnopqrstuvwxyz";

            //calculated config
            baseNum = alphabet.Length;

            for (long i = 0; i < length; i++)
            {
                double s = 1 * Math.Pow(baseNum, i);
                double e = baseNum * Math.Pow(baseNum, i);
                start += (long)s;
                end += (long)e;
            }

            //performance monitor
            numToHash = end - start;
            previousNumToHash = numToHash;
            Task t = Task.Factory.StartNew(() =>
              {
                  while (!ansFound)
                  {
                      speed = (previousNumToHash - numToHash) / 10;
                      previousNumToHash = numToHash;
                      Console.WriteLine($"Time now is {DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss")} still {numToHash} to encrypt in {speed}");
                      Thread.Sleep(10000);
                  }
              });
            //start to brute force
            Parallel.For(start, end, (n, state) =>
                {
                    numToHash--;
                    if (Encrypt(ConvertToBase(n)).Equals(hashed))
                    {
                        ansFound = true;
                        Console.WriteLine($"{n} ,{ConvertToBase(n)}, {Encrypt(ConvertToBase(n))}");
                    }
                    else if (ansFound)
                    {
                        state.Stop();
                    }
                });
            t.Wait();
        }

        public static String ConvertToBase(long dec)
        {
            StringBuilder sb = new StringBuilder();
            while (dec != 0)
            {
                // start with 1 and end with base number
                long remainder = dec % baseNum == 0 ? baseNum : dec % baseNum;
                // dictonary with non-sequecy alphatbet
                char res = (char)alphabet[(int)remainder - 1];
                sb.Insert(0, res);
                // confirm that the dec equal to 0 when end
                dec = (dec - remainder) / baseNum;
            }
            return sb.ToString();
        }

        public static string Encrypt(string password)
        {
            using (var hashAlgorithm = SHA256.Create())
            {
                Encoding encoder = Encoding.UTF8;
                byte[] bPassword = encoder.GetBytes(password);
                byte[] bHashedPassword = hashAlgorithm.ComputeHash(bPassword);
                return BitConverter.ToString(bHashedPassword).Replace("-", string.Empty).ToLower();
            }

        }
    }
}
