static int GetRandomSeed( )
{
    byte[] bytes = new byte[4];
    System.Security.Cryptography.RNGCryptoServiceProvider rng = new System.Security.Cryptography.RNGCryptoServiceProvider();
    rng.GetBytes(bytes);
    return BitConverter.ToInt32(bytes, 0);
}

Random random = new Random(GetRandomSeed());